package br.estudo.nfe.produto;

import java.math.BigDecimal;
import java.util.Optional;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Produto extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 20)
    public String codigo;

    @Column(nullable = false, length = 120)
    public String descricao;

    @Column(nullable = false, length = 8)
    public String ncm;

    @Column(nullable = false, length = 4)
    public String cfop;

    @Column(nullable = false, length = 6)
    public String unidade;

    @Column(name = "valor_unitario", nullable = false, precision = 15, scale = 2)
    public BigDecimal valorUnitario;

    public static Optional<Produto> buscarPorCodigo(String codigo) {
        return find("codigo", codigo).firstResultOptional();
    }
}
