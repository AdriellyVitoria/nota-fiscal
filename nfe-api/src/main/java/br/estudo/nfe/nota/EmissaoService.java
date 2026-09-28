package br.estudo.nfe.nota;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;

import br.estudo.nfe.consulta.ConsultaStatusService;
import br.estudo.nfe.erro.NegocioException;
import br.estudo.nfe.erro.SefazIndisponivelException;
import br.estudo.nfe.evento.NotaAutorizadaEvento;
import br.estudo.nfe.evento.OutboxService;
import br.estudo.nfe.sefaz.RespostaSefaz;
import br.estudo.nfe.sefaz.SefazClient;
import br.estudo.nfe.xml.NotaXmlMapper;
import br.estudo.nfe.xml.XmlService;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmissaoService {

    private static final Logger LOG = Logger.getLogger(EmissaoService.class);

    private static final Set<StatusNota> PODE_EMITIR = Set.of(StatusNota.RASCUNHO, StatusNota.REJEITADA);

    @Inject
    NotaFiscalService notas;

    @Inject
    NotaXmlMapper xmlMapper;

    @Inject
    XmlService xmlService;

    @Inject
    SefazClient sefaz;

    @Inject
    OutboxService outbox;

    @Inject
    ConsultaStatusService consultaStatus;

    public NotaFiscal emitir(Long id) {
        NotaFiscal nota = notas.buscar(id);
        if (!PODE_EMITIR.contains(nota.status)) {
            throw new NegocioException("Só é possível emitir nota em RASCUNHO ou REJEITADA (status atual: " + nota.status + ")");
        }

        String xml = xmlService.gerar(xmlMapper.mapear(nota));
        List<String> erros = xmlService.validar(xml);
        if (!erros.isEmpty()) {
            throw new NegocioException("XML da nota inválido: " + String.join("; ", erros));
        }

        RespostaSefaz resposta;
        try {
            resposta = sefaz.enviar(xml);
        } catch (RuntimeException e) {
            LOG.warnf("Falha ao enviar nota %d ao SEFAZ: %s", id, e.toString());
            throw new SefazIndisponivelException(e);
        }

        NotaFiscal atualizada = QuarkusTransaction.requiringNew().call(() -> {
            registrarEnvio(id, xml, resposta);
            return NotaFiscal.buscarCompleta(id).orElseThrow();
        });
        consultaStatus.invalidar(atualizada.chaveAcesso);
        return atualizada;
    }

    @Scheduled(every = "{nfe.sefaz.intervalo-consulta}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void consultarPendentes() {
        List<Pendente> pendentes = QuarkusTransaction.requiringNew().call(() -> NotaFiscal
                .<NotaFiscal>list("status", StatusNota.ENVIADA).stream()
                .map(n -> new Pendente(n.id, n.recibo))
                .toList());

        for (Pendente pendente : pendentes) {
            try {
                atualizarRetorno(pendente.id(), pendente.recibo());
            } catch (RuntimeException e) {
                LOG.warnf("Falha ao consultar recibo %s: %s", pendente.recibo(), e.toString());
            }
        }
    }

    public void atualizarRetorno(Long id, String recibo) {
        RespostaSefaz resposta = sefaz.consultar(recibo);
        if (resposta.emProcessamento()) {
            return;
        }
        String chaveAcesso = QuarkusTransaction.requiringNew().call(() -> registrarRetorno(id, resposta));
        consultaStatus.invalidar(chaveAcesso);
        LOG.infof("Nota %d: %s", id, resposta.descricao());
    }

    private void registrarEnvio(Long id, String xml, RespostaSefaz resposta) {
        NotaFiscal nota = NotaFiscal.findById(id);
        nota.xml = xml;
        if (resposta.loteRecebido()) {
            nota.status = StatusNota.ENVIADA;
            nota.recibo = resposta.recibo();
            nota.motivo = null;
        } else {
            nota.status = StatusNota.REJEITADA;
            nota.motivo = resposta.descricao();
        }
    }

    private String registrarRetorno(Long id, RespostaSefaz resposta) {
        NotaFiscal nota = NotaFiscal.findById(id);
        if (resposta.autorizada()) {
            nota.status = StatusNota.AUTORIZADA;
            nota.protocolo = resposta.protocolo();
            nota.motivo = null;
            outbox.registrar(NotaAutorizadaEvento.TIPO, nota.chaveAcesso,
                    NotaAutorizadaEvento.de(nota, OffsetDateTime.now(NotaFiscalService.FUSO_BRASILIA)));
        } else {
            nota.status = StatusNota.REJEITADA;
            nota.motivo = resposta.descricao();
        }
        return nota.chaveAcesso;
    }

    private record Pendente(Long id, String recibo) {
    }
}
