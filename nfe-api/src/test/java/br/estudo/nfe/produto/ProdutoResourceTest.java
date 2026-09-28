package br.estudo.nfe.produto;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;

@QuarkusTest
@TestSecurity(user = "maria", roles = "emissor")
class ProdutoResourceTest {

    @Test
    void listaProdutosCadastradosPelaMigracao() {
        given()
                .when().get("/produtos")
                .then()
                .statusCode(200)
                .body("codigo", hasItem("P001"));
    }

    @Test
    void devolve404ParaProdutoInexistente() {
        given()
                .when().get("/produtos/999999")
                .then()
                .statusCode(404)
                .body("mensagem", containsString("não encontrado"));
    }

    @Test
    void criaProdutoEDevolve201ComLocation() {
        String json = """
                {"codigo":"T100","descricao":"Cabo HDMI","ncm":"85444200","cfop":"5102",
                 "unidade":"UN","valorUnitario":29.90}
                """;
        given()
                .contentType(ContentType.JSON).body(json)
                .when().post("/produtos")
                .then()
                .statusCode(201)
                .header("Location", containsString("/produtos/"))
                .body("descricao", equalTo("Cabo HDMI"));
    }

    @Test
    void rejeitaProdutoInvalidoCom400() {
        String json = """
                {"codigo":"","descricao":"Sem código","ncm":"123","cfop":"5102",
                 "unidade":"UN","valorUnitario":-1}
                """;
        given()
                .contentType(ContentType.JSON).body(json)
                .when().post("/produtos")
                .then()
                .statusCode(400);
    }

    @Test
    void rejeitaCodigoDuplicadoCom422() {
        String json = """
                {"codigo":"P001","descricao":"Duplicado","ncm":"84713012","cfop":"5102",
                 "unidade":"UN","valorUnitario":10.00}
                """;
        given()
                .contentType(ContentType.JSON).body(json)
                .when().post("/produtos")
                .then()
                .statusCode(422);
    }
}
