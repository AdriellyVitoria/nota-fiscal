package br.estudo.nfe.sefaz;

public record RespostaSefaz(String codigoStatus, String motivo, String recibo, String protocolo) {

    static final String AUTORIZADO = "100";
    static final String LOTE_RECEBIDO = "103";
    static final String LOTE_EM_PROCESSAMENTO = "105";

    public boolean loteRecebido() {
        return LOTE_RECEBIDO.equals(codigoStatus);
    }

    public boolean emProcessamento() {
        return LOTE_EM_PROCESSAMENTO.equals(codigoStatus);
    }

    public boolean autorizada() {
        return AUTORIZADO.equals(codigoStatus);
    }

    public String descricao() {
        return codigoStatus + " - " + motivo;
    }
}
