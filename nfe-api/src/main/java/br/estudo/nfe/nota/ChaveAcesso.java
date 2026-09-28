package br.estudo.nfe.nota;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class ChaveAcesso {

    static final String MODELO_NFE = "55";

    static final int TIPO_EMISSAO_NORMAL = 1;

    private static final DateTimeFormatter ANO_MES = DateTimeFormatter.ofPattern("yyMM");

    private ChaveAcesso() {
    }

    public static String gerar(int codigoUf, OffsetDateTime dataEmissao, String cnpj,
            int serie, long numero, int codigoNumerico) {
        String semDigito = String.format("%02d", codigoUf)
                + dataEmissao.format(ANO_MES)
                + cnpj
                + MODELO_NFE
                + String.format("%03d", serie)
                + String.format("%09d", numero)
                + TIPO_EMISSAO_NORMAL
                + String.format("%08d", codigoNumerico);

        if (semDigito.length() != 43) {
            throw new IllegalArgumentException("Chave sem DV deveria ter 43 dígitos: " + semDigito);
        }
        return semDigito + digitoVerificador(semDigito);
    }

    public static int digitoVerificador(String numeros) {
        int soma = 0;
        int peso = 2;
        for (int i = numeros.length() - 1; i >= 0; i--) {
            soma += Character.getNumericValue(numeros.charAt(i)) * peso;
            peso = (peso == 9) ? 2 : peso + 1;
        }
        int resto = soma % 11;
        return (resto == 0 || resto == 1) ? 0 : 11 - resto;
    }
}
