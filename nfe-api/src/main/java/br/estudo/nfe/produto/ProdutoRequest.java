package br.estudo.nfe.produto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProdutoRequest(
        @NotBlank @Size(max = 20) String codigo,
        @NotBlank @Size(max = 120) String descricao,
        @NotBlank @Pattern(regexp = "\\d{8}", message = "NCM deve ter 8 dígitos") String ncm,
        @NotBlank @Pattern(regexp = "\\d{4}", message = "CFOP deve ter 4 dígitos") String cfop,
        @NotBlank @Size(max = 6) String unidade,
        @NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal valorUnitario) {
}
