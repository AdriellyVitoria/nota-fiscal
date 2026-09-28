package br.estudo.nfe.nota;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Monta a chave de acesso da NF-e (44 dígitos), que identifica a nota em todo o país.
 *
 * <pre>
 * cUF(2) + AAMM(4) + CNPJ(14) + modelo(2) + série(3) + número(9) + tpEmis(1) + código(8) + DV(1)
 * </pre>
 *
 * O último dígito (DV) é calculado pelo módulo 11 sobre os 43 anteriores.
 */
public final class ChaveAcesso {

    /** Modelo 55 = NF-e (o 65 seria NFC-e, a nota do consumidor). */
    static final String MODELO_NFE = "55";

    /** 1 = emissão normal (outros valores são para contingência). */
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

    /**
     * Módulo 11: multiplica cada dígito, da direita para a esquerda, pelos pesos 2,3,...,9,2,3...,
     * soma tudo e pega o resto da divisão por 11. DV = 11 - resto (se resto for 0 ou 1, DV = 0).
     */
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
