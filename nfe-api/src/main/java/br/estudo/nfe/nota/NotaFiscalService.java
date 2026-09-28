package br.estudo.nfe.nota;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;

import br.estudo.nfe.cliente.Cliente;
import br.estudo.nfe.cliente.ClienteService;
import br.estudo.nfe.config.EmitenteConfig;
import br.estudo.nfe.erro.NegocioException;
import br.estudo.nfe.erro.RecursoNaoEncontradoException;
import br.estudo.nfe.produto.Produto;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class NotaFiscalService {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Inject
    EmitenteConfig emitente;

    @Inject
    ClienteService clienteService;

    public List<NotaFiscal> listar(StatusNota status, int pagina, int tamanho) {
        return NotaFiscal.listar(status, pagina, tamanho);
    }

    public NotaFiscal buscar(Long id) {
        return NotaFiscal.buscarCompleta(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nota " + id + " não encontrada"));
    }

    /**
     * Cria a nota em RASCUNHO. Tudo numa transação: se qualquer item falhar,
     * nada é gravado (nem a nota, nem os itens) — atomicidade.
     */
    @Transactional
    public NotaFiscal criar(CriarNotaRequest pedido) {
        Cliente cliente = clienteService.buscar(pedido.clienteId());

        NotaFiscal nota = new NotaFiscal();
        nota.cliente = cliente;
        nota.status = StatusNota.RASCUNHO;
        nota.serie = emitente.serie();
        nota.numero = proximoNumero();
        nota.dataEmissao = OffsetDateTime.now();

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

    /** Só rascunho pode ser excluído; nota enviada ao SEFAZ tem que ser cancelada. */
    @Transactional
    public void excluir(Long id) {
        NotaFiscal nota = buscar(id);
        if (nota.status != StatusNota.RASCUNHO) {
            throw new NegocioException("Só é possível excluir nota em RASCUNHO (status atual: " + nota.status + ")");
        }
        nota.delete();
    }

    /** Número sequencial vindo de uma SEQUENCE do Postgres: atômico mesmo com várias instâncias da API. */
    private long proximoNumero() {
        return ((Number) Panache.getEntityManager()
                .createNativeQuery("select nextval('nota_numero_seq')")
                .getSingleResult()).longValue();
    }

    /** Código aleatório de 8 dígitos da chave; pela regra da SEFAZ não pode ser igual ao número da nota. */
    private int codigoNumerico(long numero) {
        int codigo;
        do {
            codigo = RANDOM.nextInt(100_000_000);
        } while (codigo == numero);
        return codigo;
    }
}
