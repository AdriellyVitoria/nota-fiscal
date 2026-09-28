package br.estudo.nfe.produto;

import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String codigo,
        String descricao,
        String ncm,
        String cfop,
        String unidade,
        BigDecimal valorUnitario) {

    public static ProdutoResponse de(Produto p) {
        return new ProdutoResponse(p.id, p.codigo, p.descricao, p.ncm, p.cfop, p.unidade, p.valorUnitario);
    }
}
