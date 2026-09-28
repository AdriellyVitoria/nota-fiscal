package br.estudo.nfe.nota;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.estudo.nfe.cliente.Cliente;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Page;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "nota_fiscal")
public class NotaFiscal extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public Long numero;

    @Column(nullable = false)
    public Integer serie;

    @Column(name = "chave_acesso", nullable = false, unique = true, length = 44)
    public String chaveAcesso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public StatusNota status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    public Cliente cliente;

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ItemNota> itens = new ArrayList<>();

    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    public BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "data_emissao", nullable = false)
    public OffsetDateTime dataEmissao;

    @Column(length = 20)
    public String protocolo;

    @Column(length = 255)
    public String motivo;

    @Column(columnDefinition = "text")
    public String xml;

    public void adicionarItem(ItemNota item) {
        item.nota = this;
        itens.add(item);
        valorTotal = itens.stream()
                .map(i -> i.valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Optional<NotaFiscal> buscarCompleta(Long id) {
        return find("""
                select distinct n from NotaFiscal n
                join fetch n.cliente
                left join fetch n.itens i
                left join fetch i.produto
                where n.id = ?1""", id)
                .firstResultOptional();
    }

    public static List<NotaFiscal> listar(StatusNota status, int pagina, int tamanho) {
        String base = "select n from NotaFiscal n join fetch n.cliente";
        var query = (status == null)
                ? find(base + " order by n.numero desc")
                : find(base + " where n.status = ?1 order by n.numero desc", status);
        return query.page(Page.of(pagina, tamanho)).list();
    }
}
