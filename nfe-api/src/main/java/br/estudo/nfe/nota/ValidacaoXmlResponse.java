package br.estudo.nfe.nota;

import java.util.List;

public record ValidacaoXmlResponse(boolean valido, List<String> erros) {

    public static ValidacaoXmlResponse de(List<String> erros) {
        return new ValidacaoXmlResponse(erros.isEmpty(), erros);
    }
}
