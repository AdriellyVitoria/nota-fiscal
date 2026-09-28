package br.estudo.nfe.nota;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record NotaResponse(
        Long id,
        Long numero,
        Integer serie,
        String chaveAcesso,
        StatusNota status,
        Long clienteId,
        String clienteNome,
        BigDecimal valorTotal,
        OffsetDateTime dataEmissao,
        String protocolo,
        String motivo,
        List<Item> itens) {

    public record Item(
            String produtoCodigo,
            String descricao,
            BigDecimal quantidade,
            BigDecimal valorUnitario,
            BigDecimal valorTotal) {
    }

    public static NotaResponse de(NotaFiscal n) {
        List<Item> itens = n.itens.stream()
                .map(i -> new Item(i.produto.codigo, i.produto.descricao, i.quantidade, i.valorUnitario, i.valorTotal))
                .toList();
        return new NotaResponse(n.id, n.numero, n.serie, n.chaveAcesso, n.status,
                n.cliente.id, n.cliente.nome, n.valorTotal, n.dataEmissao, n.protocolo, n.motivo, itens);
    }
}
