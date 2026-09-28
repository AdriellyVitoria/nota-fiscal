package br.estudo.nfe.nota;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record NotaResumoResponse(
        Long id,
        Long numero,
        Integer serie,
        String chaveAcesso,
        StatusNota status,
        String clienteNome,
        BigDecimal valorTotal,
        OffsetDateTime dataEmissao) {

    public static NotaResumoResponse de(NotaFiscal n) {
        return new NotaResumoResponse(n.id, n.numero, n.serie, n.chaveAcesso, n.status,
                n.cliente.nome, n.valorTotal, n.dataEmissao);
    }
}
