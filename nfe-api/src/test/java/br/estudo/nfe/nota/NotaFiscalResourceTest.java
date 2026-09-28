package br.estudo.nfe.nota;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class NotaFiscalResourceTest {

    @Test
    void criaNotaEmRascunhoComTotalCalculadoEChaveDe44Digitos() {
        // 2 x Mouse (80,50) + 1 x Teclado (150,00) = 311,00
        String json = """
                {"clienteId":1,"itens":[
                    {"produtoId":2,"quantidade":2},
                    {"produtoId":3,"quantidade":1}
                ]}
                """;
        given()
                .contentType(ContentType.JSON).body(json)
                .when().post("/notas")
                .then()
                .statusCode(201)
                .body("status", equalTo("RASCUNHO"))
                .body("valorTotal", equalTo(311.00f))
                .body("chaveAcesso.length()", equalTo(44))
                .body("itens", hasSize(2));
    }

    @Test
    void rejeitaNotaComProdutoInexistente() {
        String json = """
                {"clienteId":1,"itens":[{"produtoId":999999,"quantidade":1}]}
                """;
        given()
                .contentType(ContentType.JSON).body(json)
                .when().post("/notas")
                .then()
                .statusCode(422);
    }

    @Test
    void rejeitaNotaSemItensCom400() {
        given()
                .contentType(ContentType.JSON).body("{\"clienteId\":1,\"itens\":[]}")
                .when().post("/notas")
                .then()
                .statusCode(400);
    }
}
