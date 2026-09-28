package br.estudo.nfe.evento;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import br.estudo.nfe.auditoria.AuditoriaConsumer;
import br.estudo.nfe.auditoria.AuditoriaNfe;
import br.estudo.nfe.nota.EmissaoService;
import br.estudo.nfe.sefaz.RespostaSefaz;
import br.estudo.nfe.sefaz.SefazClient;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
@TestSecurity(user = "maria", roles = "emissor")
class EventoKafkaTest {

    @InjectMock
    SefazClient sefaz;

    @Inject
    EmissaoService emissao;

    @Inject
    OutboxPublicador publicador;

    @Inject
    AuditoriaConsumer consumidor;

    @Test
    void notaAutorizadaGeraEventoNoOutboxQueChegaNaAuditoriaPeloKafka() {
        String chave = autorizarNota("350000000000501");

        EventoOutbox evento = eventoDaChave(chave);
        assertNotNull(evento);
        assertNull(evento.publicadoEm);

        publicador.publicarPendentes();

        assertNotNull(eventoDaChave(chave).publicadoEm);
        await().atMost(Duration.ofSeconds(20)).until(() -> auditoriaDaChave(chave) != null);
        assertEquals("135260000000501", auditoriaDaChave(chave).protocolo);
    }

    @Test
    void consumidorEhIdempotente() throws Exception {
        String chave = autorizarNota("350000000000502");
        String payload = eventoDaChave(chave).payload;

        consumidor.consumir(new ConsumerRecord<>("nfe-autorizada", 0, 1000L, chave, payload));
        consumidor.consumir(new ConsumerRecord<>("nfe-autorizada", 0, 1000L, chave, payload));

        long quantidade = QuarkusTransaction.requiringNew().call(() -> AuditoriaNfe.count("chaveAcesso", chave));
        assertEquals(1, quantidade);
    }

    private String autorizarNota(String recibo) {
        String protocolo = "1352600000" + recibo.substring(recibo.length() - 5);
        when(sefaz.enviar(anyString())).thenReturn(new RespostaSefaz("103", "Lote recebido com sucesso", recibo, null));
        when(sefaz.consultar(recibo)).thenReturn(new RespostaSefaz("100", "Autorizado o uso da NF-e", recibo, protocolo));

        int id = given()
                .contentType(ContentType.JSON)
                .body("{\"clienteId\":2,\"itens\":[{\"produtoId\":4,\"quantidade\":1}]}")
                .when().post("/notas")
                .then().statusCode(201)
                .extract().path("id");
        given().when().post("/notas/" + id + "/emitir").then().statusCode(202);
        emissao.atualizarRetorno((long) id, recibo);

        return given().when().get("/notas/" + id).then().extract().path("chaveAcesso");
    }

    private EventoOutbox eventoDaChave(String chave) {
        return QuarkusTransaction.requiringNew().call(() -> EventoOutbox.<EventoOutbox>find("chave", chave).firstResult());
    }

    private AuditoriaNfe auditoriaDaChave(String chave) {
        return QuarkusTransaction.requiringNew().call(() -> AuditoriaNfe.<AuditoriaNfe>find("chaveAcesso", chave).firstResult());
    }
}
