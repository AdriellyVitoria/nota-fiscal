package br.estudo.nfe.nota;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

/** Teste unitário puro (sem subir o Quarkus): roda em milissegundos. */
class ChaveAcessoTest {

    @Test
    void calculaDigitoVerificadorDoExemploDoManualDaNFe() {
        // Chave de exemplo do Manual de Orientação do Contribuinte: 5206...0161 + DV 5
        assertEquals(5, ChaveAcesso.digitoVerificador("5206043300991100250655012000000780026730161"));
    }

    @Test
    void geraChaveCom44DigitosNaOrdemCerta() {
        OffsetDateTime data = OffsetDateTime.of(2026, 9, 28, 10, 0, 0, 0, ZoneOffset.ofHours(-3));

        String chave = ChaveAcesso.gerar(35, data, "11222333000181", 1, 123, 45678901);

        assertEquals(44, chave.length());
        assertTrue(chave.matches("\\d{44}"));
        assertEquals("35", chave.substring(0, 2));             // UF
        assertEquals("2609", chave.substring(2, 6));           // AAMM
        assertEquals("11222333000181", chave.substring(6, 20)); // CNPJ
        assertEquals("55", chave.substring(20, 22));           // modelo
        assertEquals("001", chave.substring(22, 25));          // série
        assertEquals("000000123", chave.substring(25, 34));    // número
        assertEquals(ChaveAcesso.digitoVerificador(chave.substring(0, 43)),
                Character.getNumericValue(chave.charAt(43)));
    }
}
