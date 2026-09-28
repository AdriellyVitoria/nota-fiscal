package br.estudo.nfe.consulta;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import br.estudo.nfe.sefaz.RespostaSefaz;
import br.estudo.nfe.sefaz.SefazClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

@QuarkusTest
@TestSecurity(user = "maria", roles = "emissor")
class ConsultaCacheTest {

    @InjectMock
    SefazClient sefaz;

    @Test
    void primeiraConsultaVaiAoBancoESegundaVemDoCache() {
        String chave = criarNota().getString("chaveAcesso");

        given().when().get("/consulta/" + chave)
                .then().statusCode(200).header("X-Cache", "MISS").body("status", equalTo("RASCUNHO"));

        given().when().get("/consulta/" + chave)
                .then().statusCode(200).header("X-Cache", "HIT").body("status", equalTo("RASCUNHO"));
    }

    @Test
    void mudancaDeStatusInvalidaOCache() {
        when(sefaz.enviar(anyString()))
                .thenReturn(new RespostaSefaz("103", "Lote recebido com sucesso", "350000000000701", null));
        JsonPath nota = criarNota();
        String chave = nota.getString("chaveAcesso");

        given().when().get("/consulta/" + chave).then().header("X-Cache", "MISS");
        given().when().get("/consulta/" + chave).then().header("X-Cache", "HIT");

        given().when().post("/notas/" + nota.getInt("id") + "/emitir").then().statusCode(202);

        given().when().get("/consulta/" + chave)
                .then().header("X-Cache", "MISS").body("status", equalTo("ENVIADA"));
    }

    @Test
    void notaExcluidaSaiDoCache() {
        JsonPath nota = criarNota();
        String chave = nota.getString("chaveAcesso");
        given().when().get("/consulta/" + chave).then().statusCode(200);

        given().when().delete("/notas/" + nota.getInt("id")).then().statusCode(204);

        given().when().get("/consulta/" + chave).then().statusCode(404);
    }

    @Test
    void chaveInvalidaDevolve400() {
        given().when().get("/consulta/123").then().statusCode(400);
    }

    private JsonPath criarNota() {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"clienteId\":1,\"itens\":[{\"produtoId\":2,\"quantidade\":1}]}")
                .when().post("/notas")
                .then().statusCode(201)
                .extract().jsonPath();
    }
}
