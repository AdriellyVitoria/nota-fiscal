package br.estudo.nfe.cliente;

import java.net.URI;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/clientes")
public class ClienteResource {

    @Inject
    ClienteService service;

    @GET
    public List<ClienteResponse> listar() {
        return service.listar().stream().map(ClienteResponse::de).toList();
    }

    @GET
    @Path("/{id}")
    public ClienteResponse buscar(@PathParam("id") Long id) {
        return ClienteResponse.de(service.buscar(id));
    }

    @POST
    public Response criar(@Valid ClienteRequest dados) {
        Cliente cliente = service.criar(dados);
        return Response.created(URI.create("/clientes/" + cliente.id))
                .entity(ClienteResponse.de(cliente))
                .build();
    }
}
