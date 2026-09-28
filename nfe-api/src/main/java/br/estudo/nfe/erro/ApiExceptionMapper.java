package br.estudo.nfe.erro;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

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
