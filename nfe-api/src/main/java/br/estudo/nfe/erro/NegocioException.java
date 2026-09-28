package br.estudo.nfe.erro;

public class NegocioException extends ApiException {

    public NegocioException(String mensagem) {
        super(422, mensagem);
    }
}
