package br.estudo.nfe.erro;

/**
 * Exceção base da aplicação: carrega o status HTTP que deve ser devolvido.
 * É "unchecked" (RuntimeException), então o @Transactional faz rollback automaticamente.
 */
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
