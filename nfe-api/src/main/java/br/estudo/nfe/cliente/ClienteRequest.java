package br.estudo.nfe.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank @Pattern(regexp = "\\d{11}|\\d{14}", message = "Documento deve ser CPF (11 dígitos) ou CNPJ (14 dígitos)") String documento,
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Pattern(regexp = "[A-Z]{2}", message = "UF deve ter 2 letras maiúsculas") String uf,
        @Email @Size(max = 120) String email) {
}
