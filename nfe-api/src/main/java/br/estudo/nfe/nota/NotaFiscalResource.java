package br.estudo.nfe.nota;

import java.net.URI;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/notas")
public class NotaFiscalResource {

    @Inject
    NotaFiscalService service;

    /** Ex.: GET /notas?status=RASCUNHO&pagina=0&tamanho=20 */
    @GET
    public List<NotaResumoResponse> listar(
            @QueryParam("status") StatusNota status,
            @QueryParam("pagina") @DefaultValue("0") @Min(0) int pagina,
            @QueryParam("tamanho") @DefaultValue("20") @Min(1) @Max(100) int tamanho) {
        return service.listar(status, pagina, tamanho).stream().map(NotaResumoResponse::de).toList();
    }

    @GET
    @Path("/{id}")
    public NotaResponse buscar(@PathParam("id") Long id) {
        return NotaResponse.de(service.buscar(id));
    }

    @POST
    public Response criar(@Valid CriarNotaRequest pedido) {
        NotaFiscal nota = service.criar(pedido);
        return Response.created(URI.create("/notas/" + nota.id))
                .entity(NotaResponse.de(nota))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public void excluir(@PathParam("id") Long id) {
        service.excluir(id);
    }
}
