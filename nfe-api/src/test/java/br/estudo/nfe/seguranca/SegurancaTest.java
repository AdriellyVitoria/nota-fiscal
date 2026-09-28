package br.estudo.nfe.seguranca;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;

@QuarkusTest
class SegurancaTest {

    private static final String NOTA = "{\"clienteId\":1,\"itens\":[{\"produtoId\":2,\"quantidade\":1}]}";

    @Test
    void semTokenDevolve401() {
        given().when().get("/notas").then().statusCode(401);
        given().when().get("/produtos").then().statusCode(401);
    }

    @Test
    void healthCheckContinuaPublico() {
        given().when().get("/q/health").then().statusCode(200);
    }

    @Test
    @TestSecurity(user = "joao", roles = "consulta")
    void perfilConsultaPodeLer() {
        given().when().get("/notas").then().statusCode(200);
        given().when().get("/produtos").then().statusCode(200);
    }

    @Test
    @TestSecurity(user = "joao", roles = "consulta")
    void perfilConsultaNaoPodeCriarNemEmitirNota() {
        given().contentType(ContentType.JSON).body(NOTA)
                .when().post("/notas")
                .then().statusCode(403);

        given().when().post("/notas/1/emitir").then().statusCode(403);
        given().when().delete("/produtos/1").then().statusCode(403);
    }

    @Test
    @TestSecurity(user = "maria", roles = "emissor")
    void perfilEmissorCriaNotaERegistraQuemCriou() {
        given().contentType(ContentType.JSON).body(NOTA)
                .when().post("/notas")
                .then()
                .statusCode(201)
                .body("criadaPor", equalTo("maria"));
    }

    @Test
    @TestSecurity(user = "maria", roles = "emissor")
    void usuarioAtualMostraNomeEPapeis() {
        given().when().get("/usuario")
                .then()
                .statusCode(200)
                .body("nome", equalTo("maria"))
                .body("papeis", hasItem("emissor"));
    }
}
