package br.estudo.nfe.cliente;

import java.util.List;

import br.estudo.nfe.erro.NegocioException;
import br.estudo.nfe.erro.RecursoNaoEncontradoException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ClienteService {

    public List<Cliente> listar() {
        return Cliente.listAll();
    }

    public Cliente buscar(Long id) {
        return Cliente.<Cliente>findByIdOptional(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente " + id + " não encontrado"));
    }

    @Transactional
    public Cliente criar(ClienteRequest dados) {
        if (Cliente.existeDocumento(dados.documento())) {
            throw new NegocioException("Já existe cliente com o documento " + dados.documento());
        }
        Cliente cliente = new Cliente();
        cliente.documento = dados.documento();
        cliente.nome = dados.nome();
        cliente.uf = dados.uf();
        cliente.email = dados.email();
        cliente.persist();
        return cliente;
    }
}
