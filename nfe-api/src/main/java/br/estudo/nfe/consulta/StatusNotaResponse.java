package br.estudo.nfe.consulta;

import java.math.BigDecimal;

import br.estudo.nfe.nota.NotaFiscal;
import br.estudo.nfe.nota.StatusNota;

public record StatusNotaResponse(
        String chaveAcesso,
        Long numero,
        Integer serie,
        StatusNota status,
        String protocolo,
        String motivo,
        BigDecimal valorTotal) {

    public static StatusNotaResponse de(NotaFiscal n) {
        return new StatusNotaResponse(n.chaveAcesso, n.numero, n.serie, n.status, n.protocolo, n.motivo, n.valorTotal);
    }
}
