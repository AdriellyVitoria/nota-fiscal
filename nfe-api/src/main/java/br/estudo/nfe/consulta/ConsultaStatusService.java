package br.estudo.nfe.consulta;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.estudo.nfe.erro.RecursoNaoEncontradoException;
import br.estudo.nfe.nota.NotaFiscal;
import br.estudo.nfe.nota.StatusNota;
import io.quarkus.infinispan.client.Remote;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConsultaStatusService {

    private static final Logger LOG = Logger.getLogger(ConsultaStatusService.class);

    private static final Set<StatusNota> STATUS_FINAIS = Set.of(StatusNota.AUTORIZADA, StatusNota.CANCELADA);

    @Inject
    @Remote("status-nota")
    RemoteCache<String, String> cache;

    @Inject
    ObjectMapper json;

    @ConfigProperty(name = "nfe.cache.ttl-status-final")
    Duration ttlFinal;

    @ConfigProperty(name = "nfe.cache.ttl-status-transitorio")
    Duration ttlTransitorio;

    public ResultadoConsulta consultar(String chaveAcesso) {
        StatusNotaResponse emCache = lerDoCache(chaveAcesso);
        if (emCache != null) {
            return new ResultadoConsulta(emCache, true);
        }

        StatusNotaResponse status = QuarkusTransaction.requiringNew().call(() -> NotaFiscal
                .<NotaFiscal>find("chaveAcesso", chaveAcesso)
                .firstResultOptional()
                .map(StatusNotaResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nota com chave " + chaveAcesso + " não encontrada")));

        gravarNoCache(status);
        return new ResultadoConsulta(status, false);
    }

    public void invalidar(String chaveAcesso) {
        try {
            cache.remove(chaveAcesso);
        } catch (RuntimeException e) {
            LOG.warnf("Falha ao invalidar cache da chave %s: %s", chaveAcesso, e.toString());
        }
    }

    private StatusNotaResponse lerDoCache(String chaveAcesso) {
        try {
            String valor = cache.get(chaveAcesso);
            return valor == null ? null : json.readValue(valor, StatusNotaResponse.class);
        } catch (RuntimeException | JsonProcessingException e) {
            LOG.warnf("Cache indisponível, consultando o banco: %s", e.toString());
            return null;
        }
    }

    private void gravarNoCache(StatusNotaResponse status) {
        Duration ttl = STATUS_FINAIS.contains(status.status()) ? ttlFinal : ttlTransitorio;
        try {
            cache.put(status.chaveAcesso(), json.writeValueAsString(status), ttl.toSeconds(), TimeUnit.SECONDS);
        } catch (RuntimeException | JsonProcessingException e) {
            LOG.warnf("Falha ao gravar no cache: %s", e.toString());
        }
    }

    public record ResultadoConsulta(StatusNotaResponse status, boolean doCache) {
    }
}
