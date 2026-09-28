package br.estudo.nfe.erro;

/** O recurso pedido não existe → HTTP 404. */
public class RecursoNaoEncontradoException extends ApiException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(404, mensagem);
    }
}
