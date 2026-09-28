package br.estudo.nfe.produto;

import java.util.List;

import br.estudo.nfe.erro.NegocioException;
import br.estudo.nfe.erro.RecursoNaoEncontradoException;
import br.estudo.nfe.nota.ItemNota;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProdutoService {

    public List<Produto> listar() {
        return Produto.listAll();
    }

    public Produto buscar(Long id) {
        return Produto.<Produto>findByIdOptional(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto " + id + " não encontrado"));
    }

    @Transactional
    public Produto criar(ProdutoRequest dados) {
        if (Produto.buscarPorCodigo(dados.codigo()).isPresent()) {
            throw new NegocioException("Já existe produto com o código " + dados.codigo());
        }
        Produto produto = new Produto();
        copiar(dados, produto);
        produto.persist();
        return produto;
    }

    @Transactional
    public Produto atualizar(Long id, ProdutoRequest dados) {
        Produto produto = buscar(id);
        Produto.buscarPorCodigo(dados.codigo())
                .filter(outro -> !outro.id.equals(id))
                .ifPresent(outro -> {
                    throw new NegocioException("Já existe produto com o código " + dados.codigo());
                });
        copiar(dados, produto);
        return produto;
    }

    @Transactional
    public void remover(Long id) {
        Produto produto = buscar(id);
        if (ItemNota.count("produto.id", id) > 0) {
            throw new NegocioException("Produto " + id + " já foi usado em nota fiscal e não pode ser removido");
        }
        produto.delete();
    }

    private void copiar(ProdutoRequest dados, Produto produto) {
        produto.codigo = dados.codigo();
        produto.descricao = dados.descricao();
        produto.ncm = dados.ncm();
        produto.cfop = dados.cfop();
        produto.unidade = dados.unidade();
        produto.valorUnitario = dados.valorUnitario();
    }
}
