package br.estudo.nfe.seguranca;

import java.util.Set;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/usuario")
@Authenticated
public class UsuarioResource {

    @Inject
    SecurityIdentity identidade;

    @GET
    public UsuarioResponse atual() {
        return new UsuarioResponse(identidade.getPrincipal().getName(), identidade.getRoles());
    }

    public record UsuarioResponse(String nome, Set<String> papeis) {
    }
}
