package br.estudo.nfe.auditoria;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import br.estudo.nfe.seguranca.Perfis;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@RolesAllowed({ Perfis.EMISSOR, Perfis.CONSULTA })
@Path("/auditoria")
public class AuditoriaResource {

    @GET
    public List<AuditoriaResponse> listar() {
        return AuditoriaNfe.<AuditoriaNfe>listAll(Sort.by("id").descending()).stream()
                .map(AuditoriaResponse::de)
                .toList();
    }

    public record AuditoriaResponse(
            String chaveAcesso,
            Long numero,
            String protocolo,
            String clienteNome,
            BigDecimal valorTotal,
            OffsetDateTime autorizadaEm,
            OffsetDateTime registradaEm,
            Integer particao,
            Long offset) {

        static AuditoriaResponse de(AuditoriaNfe a) {
            return new AuditoriaResponse(a.chaveAcesso, a.numero, a.protocolo, a.clienteNome, a.valorTotal,
                    a.autorizadaEm, a.registradaEm, a.particao, a.offsetKafka);
        }
    }
}
