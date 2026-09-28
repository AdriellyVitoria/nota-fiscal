package br.estudo.nfe.erro;

public class SefazIndisponivelException extends ApiException {

    public SefazIndisponivelException(Throwable causa) {
        super(503, "SEFAZ indisponível no momento. A nota continua em rascunho; tente novamente em instantes.");
        initCause(causa);
    }
}
