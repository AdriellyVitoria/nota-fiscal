package br.estudo.nfe.nota;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class NotaFiscalResourceTest {

    @Test
    void criaNotaEmRascunhoComTotalCalculadoEChaveDe44Digitos() {
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
    void geraXmlValidoEDanfeDaNota() {
        String json = """
                {"clienteId":2,"itens":[{"produtoId":1,"quantidade":1}]}
                """;
        int id = given()
                .contentType(ContentType.JSON).body(json)
                .when().post("/notas")
                .then().statusCode(201)
                .extract().path("id");

        given()
                .when().get("/notas/" + id + "/xml")
                .then()
                .statusCode(200)
                .contentType(ContentType.XML)
                .body(containsString("<CNPJ>45723174000110</CNPJ>"))
                .body(containsString("<vNF>3500.00</vNF>"));

        given()
                .when().get("/notas/" + id + "/xml/validacao")
                .then()
                .statusCode(200)
                .body("valido", equalTo(true))
                .body("erros", hasSize(0));

        given()
                .when().get("/notas/" + id + "/danfe")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("DANFE"))
                .body(containsString("3.500,00"))
                .body(containsString("45.723.174/0001-10"));
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
