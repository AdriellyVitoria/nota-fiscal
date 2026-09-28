package br.estudo.nfe.erro;

import java.time.OffsetDateTime;

/** Corpo padronizado de erro devolvido pela API. */
public record ErroResponse(int status, String mensagem, OffsetDateTime dataHora) {

    public ErroResponse(int status, String mensagem) {
        this(status, mensagem, OffsetDateTime.now());
    }
}
