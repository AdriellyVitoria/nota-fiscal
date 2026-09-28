package br.estudo.nfe.nota;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Pedido de criação de nota: para quem vender e o quê. Preços vêm do cadastro, não do cliente da API. */
public record CriarNotaRequest(
        @NotNull Long clienteId,
        @NotEmpty List<@Valid Item> itens) {

    public record Item(
            @NotNull Long produtoId,
            @NotNull @Positive BigDecimal quantidade) {
    }
}
