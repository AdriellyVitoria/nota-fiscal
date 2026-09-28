package br.estudo.nfe.erro;

public abstract class ApiException extends RuntimeException {

    private final int status;

    protected ApiException(int status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
