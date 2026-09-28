package br.estudo.nfe.nota;

import java.net.URI;
import java.util.List;

import br.estudo.nfe.seguranca.Perfis;
import jakarta.annotation.security.RolesAllowed;
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
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RolesAllowed({ Perfis.EMISSOR, Perfis.CONSULTA })
@Path("/notas")
public class NotaFiscalResource {

    @Inject
    NotaFiscalService service;

    @Inject
    EmissaoService emissao;

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

    @GET
    @Path("/{id}/xml")
    @Produces(MediaType.APPLICATION_XML)
    public String xml(@PathParam("id") Long id) {
        return service.xml(id);
    }

    @GET
    @Path("/{id}/xml/validacao")
    public ValidacaoXmlResponse validarXml(@PathParam("id") Long id) {
        return ValidacaoXmlResponse.de(service.validarXml(id));
    }

    @GET
    @Path("/{id}/danfe")
    @Produces(MediaType.TEXT_HTML)
    public String danfe(@PathParam("id") Long id) {
        return service.danfe(id);
    }

    @RolesAllowed(Perfis.EMISSOR)
    @POST
    public Response criar(@Valid CriarNotaRequest pedido) {
        NotaFiscal nota = service.criar(pedido);
        return Response.created(URI.create("/notas/" + nota.id))
                .entity(NotaResponse.de(nota))
                .build();
    }

    @RolesAllowed(Perfis.EMISSOR)
    @POST
    @Path("/{id}/emitir")
    public Response emitir(@PathParam("id") Long id) {
        NotaFiscal nota = emissao.emitir(id);
        return Response.accepted(NotaResponse.de(nota)).build();
    }

    @RolesAllowed(Perfis.EMISSOR)
    @DELETE
    @Path("/{id}")
    public void excluir(@PathParam("id") Long id) {
        service.excluir(id);
    }
}
