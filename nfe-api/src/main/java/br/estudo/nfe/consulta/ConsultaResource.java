package br.estudo.nfe.consulta;

import br.estudo.nfe.consulta.ConsultaStatusService.ResultadoConsulta;
import br.estudo.nfe.seguranca.Perfis;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@RolesAllowed({ Perfis.EMISSOR, Perfis.CONSULTA })
@Path("/consulta")
public class ConsultaResource {

    @Inject
    ConsultaStatusService service;

    @GET
    @Path("/{chaveAcesso}")
    public Response consultar(
            @PathParam("chaveAcesso") @Pattern(regexp = "\\d{44}", message = "Chave de acesso deve ter 44 dígitos") String chaveAcesso) {
        ResultadoConsulta resultado = service.consultar(chaveAcesso);
        return Response.ok(resultado.status())
                .header("X-Cache", resultado.doCache() ? "HIT" : "MISS")
                .build();
    }
}
