package br.estudo.nfe.produto;

import java.math.BigDecimal;

/** O que a API devolve sobre um produto (DTO de saída) — a entidade nunca sai direto. */
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
