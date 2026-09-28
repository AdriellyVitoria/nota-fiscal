package br.estudo.nfe.erro;

public class RecursoNaoEncontradoException extends ApiException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(404, mensagem);
    }
}
