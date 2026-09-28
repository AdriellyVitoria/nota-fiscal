package br.estudo.sefaz;

public final class CodigoStatus {

    public static final String AUTORIZADO = "100";
    public static final String LOTE_RECEBIDO = "103";
    public static final String LOTE_EM_PROCESSAMENTO = "105";
    public static final String DUPLICIDADE = "204";
    public static final String FALHA_SCHEMA = "225";
    public static final String RECIBO_INEXISTENTE = "248";
    public static final String VALOR_ACIMA_LIMITE = "999";

    private CodigoStatus() {
    }
}
