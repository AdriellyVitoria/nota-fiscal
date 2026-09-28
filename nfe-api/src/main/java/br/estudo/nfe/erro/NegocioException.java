package br.estudo.nfe.erro;

/** Os dados chegaram no formato certo, mas violam uma regra de negócio → HTTP 422. */
public class NegocioException extends ApiException {

    public NegocioException(String mensagem) {
        super(422, mensagem);
    }
}
