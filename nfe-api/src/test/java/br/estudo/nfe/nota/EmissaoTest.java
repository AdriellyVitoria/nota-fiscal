package br.estudo.nfe.nota;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import br.estudo.nfe.sefaz.RespostaSefaz;
import br.estudo.nfe.sefaz.SefazClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;

@QuarkusTest
@TestSecurity(user = "maria", roles = "emissor")
class EmissaoTest {

    @InjectMock
    SefazClient sefaz;

    @Inject
    EmissaoService emissao;

    @Test
    void emiteNotaQueFicaEnviadaEDepoisAutorizada() {
        when(sefaz.enviar(anyString()))
                .thenReturn(new RespostaSefaz("103", "Lote recebido com sucesso", "350000000000077", null));
        when(sefaz.consultar("350000000000077"))
                .thenReturn(new RespostaSefaz("100", "Autorizado o uso da NF-e", "350000000000077", "135260000000077"));
        int id = criarNota();

        given()
                .when().post("/notas/" + id + "/emitir")
                .then()
                .statusCode(202)
                .body("status", equalTo("ENVIADA"))
                .body("recibo", equalTo("350000000000077"));

        emissao.atualizarRetorno((long) id, "350000000000077");

        given()
                .when().get("/notas/" + id)
                .then()
                .statusCode(200)
                .body("status", equalTo("AUTORIZADA"))
                .body("protocolo", equalTo("135260000000077"));
    }

    @Test
    void notaRejeitadaGuardaMotivo() {
        when(sefaz.enviar(anyString()))
                .thenReturn(new RespostaSefaz("103", "Lote recebido com sucesso", "350000000000088", null));
        when(sefaz.consultar("350000000000088"))
                .thenReturn(new RespostaSefaz("204", "Rejeição: Duplicidade de NF-e", "350000000000088", null));
        int id = criarNota();

        given().when().post("/notas/" + id + "/emitir").then().statusCode(202);
        emissao.atualizarRetorno((long) id, "350000000000088");

        given()
                .when().get("/notas/" + id)
                .then()
                .body("status", equalTo("REJEITADA"))
                .body("motivo", containsString("204"));
    }

    @Test
    void sefazForaDoArDevolve503EMantemRascunho() {
        when(sefaz.enviar(anyString())).thenThrow(new ProcessingException("Connection refused"));
        int id = criarNota();

        given()
                .when().post("/notas/" + id + "/emitir")
                .then()
                .statusCode(503);

        given()
                .when().get("/notas/" + id)
                .then()
                .body("status", equalTo("RASCUNHO"));
    }

    @Test
    void naoEmiteNotaJaEnviada() {
        when(sefaz.enviar(anyString()))
                .thenReturn(new RespostaSefaz("103", "Lote recebido com sucesso", "350000000000099", null));
        int id = criarNota();
        given().when().post("/notas/" + id + "/emitir").then().statusCode(202);

        given()
                .when().post("/notas/" + id + "/emitir")
                .then()
                .statusCode(422);
    }

    @Test
    void emissaoNaoChamaSefazParaNotaInexistente() {
        given().when().post("/notas/999999/emitir").then().statusCode(404);

        verify(sefaz, never()).enviar(anyString());
    }

    private int criarNota() {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"clienteId\":1,\"itens\":[{\"produtoId\":2,\"quantidade\":1}]}")
                .when().post("/notas")
                .then().statusCode(201)
                .extract().path("id");
    }
}
