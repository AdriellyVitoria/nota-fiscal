package br.estudo.nfe.erro;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Converte qualquer ApiException em uma resposta HTTP com corpo JSON padronizado.
 * ExceptionMapper é da especificação Jakarta REST: funciona igual no Quarkus e no WildFly.
 */
@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

    @Override
    public Response toResponse(ApiException e) {
        int status = e.getStatus();
        return Response.status(status)
                .entity(new ErroResponse(status, e.getMessage()))
                .build();
    }
}
