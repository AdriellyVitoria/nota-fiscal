package br.estudo.nfe.produto;

import java.net.URI;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/** Camada HTTP: recebe a requisição, delega ao service e escolhe o status da resposta. */
@Path("/produtos")
public class ProdutoResource {

    @Inject
    ProdutoService service;

    @GET
    public List<ProdutoResponse> listar() {
        return service.listar().stream().map(ProdutoResponse::de).toList();
    }

    @GET
    @Path("/{id}")
    public ProdutoResponse buscar(@PathParam("id") Long id) {
        return ProdutoResponse.de(service.buscar(id));
    }

    @POST
    public Response criar(@Valid ProdutoRequest dados) {
        Produto produto = service.criar(dados);
        return Response.created(URI.create("/produtos/" + produto.id))
                .entity(ProdutoResponse.de(produto))
                .build();
    }

    @PUT
    @Path("/{id}")
    public ProdutoResponse atualizar(@PathParam("id") Long id, @Valid ProdutoRequest dados) {
        return ProdutoResponse.de(service.atualizar(id, dados));
    }

    @DELETE
    @Path("/{id}")
    public void remover(@PathParam("id") Long id) {
        service.remover(id);
    }
}
