package br.estudo.nfe.nota;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import br.estudo.nfe.cliente.Cliente;
import br.estudo.nfe.cliente.ClienteService;
import br.estudo.nfe.config.EmitenteConfig;
import br.estudo.nfe.erro.NegocioException;
import br.estudo.nfe.erro.RecursoNaoEncontradoException;
import br.estudo.nfe.produto.Produto;
import br.estudo.nfe.xml.DanfeService;
import br.estudo.nfe.xml.NotaXmlMapper;
import br.estudo.nfe.xml.XmlService;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class NotaFiscalService {

    private static final SecureRandom RANDOM = new SecureRandom();

    static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");

    @Inject
    EmitenteConfig emitente;

    @Inject
    ClienteService clienteService;

    @Inject
    NotaXmlMapper xmlMapper;

    @Inject
    XmlService xmlService;

    @Inject
    DanfeService danfeService;

    public List<NotaFiscal> listar(StatusNota status, int pagina, int tamanho) {
        return NotaFiscal.listar(status, pagina, tamanho);
    }

    public NotaFiscal buscar(Long id) {
        return NotaFiscal.buscarCompleta(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nota " + id + " não encontrada"));
    }

    @Transactional
    public NotaFiscal criar(CriarNotaRequest pedido) {
        Cliente cliente = clienteService.buscar(pedido.clienteId());

        NotaFiscal nota = new NotaFiscal();
        nota.cliente = cliente;
        nota.status = StatusNota.RASCUNHO;
        nota.serie = emitente.serie();
        nota.numero = proximoNumero();
        nota.dataEmissao = OffsetDateTime.now(FUSO_BRASILIA);

        for (CriarNotaRequest.Item pedidoItem : pedido.itens()) {
            Produto produto = Produto.<Produto>findByIdOptional(pedidoItem.produtoId())
                    .orElseThrow(() -> new NegocioException("Produto " + pedidoItem.produtoId() + " não existe"));
            nota.adicionarItem(ItemNota.de(produto, pedidoItem.quantidade()));
        }

        nota.chaveAcesso = ChaveAcesso.gerar(emitente.codigoUf(), nota.dataEmissao, emitente.cnpj(),
                nota.serie, nota.numero, codigoNumerico(nota.numero));
        nota.persist();
        return nota;
    }

    public String xml(Long id) {
        NotaFiscal nota = buscar(id);
        return (nota.xml != null) ? nota.xml : xmlService.gerar(xmlMapper.mapear(nota));
    }

    public List<String> validarXml(Long id) {
        return xmlService.validar(xml(id));
    }

    public String danfe(Long id) {
        return danfeService.gerarHtml(xml(id));
    }

    @Transactional
    public void excluir(Long id) {
        NotaFiscal nota = buscar(id);
        if (nota.status != StatusNota.RASCUNHO) {
            throw new NegocioException("Só é possível excluir nota em RASCUNHO (status atual: " + nota.status + ")");
        }
        nota.delete();
    }

    private long proximoNumero() {
        return ((Number) Panache.getEntityManager()
                .createNativeQuery("select nextval('nota_numero_seq')")
                .getSingleResult()).longValue();
    }

    private int codigoNumerico(long numero) {
        int codigo;
        do {
            codigo = RANDOM.nextInt(100_000_000);
        } while (codigo == numero);
        return codigo;
    }
}
