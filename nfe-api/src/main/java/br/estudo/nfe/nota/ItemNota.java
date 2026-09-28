package br.estudo.nfe.nota;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.estudo.nfe.produto.Produto;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_nota")
public class ItemNota extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nota_id")
    public NotaFiscal nota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id")
    public Produto produto;

    @Column(nullable = false, precision = 15, scale = 4)
    public BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 15, scale = 2)
    public BigDecimal valorUnitario;

    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    public BigDecimal valorTotal;

    public static ItemNota de(Produto produto, BigDecimal quantidade) {
        ItemNota item = new ItemNota();
        item.produto = produto;
        item.quantidade = quantidade;
        item.valorUnitario = produto.valorUnitario;
        item.valorTotal = quantidade.multiply(produto.valorUnitario).setScale(2, RoundingMode.HALF_UP);
        return item;
    }
}
