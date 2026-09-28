package br.estudo.nfe.produto;

import java.math.BigDecimal;
import java.util.Optional;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidade JPA no padrão Active Record do Panache: a própria classe tem os métodos de consulta
 * (Produto.findById, Produto.listAll...). Campos públicos: o Quarkus gera getters/setters no build.
 */
@Entity
public class Produto extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 20)
    public String codigo;

    @Column(nullable = false, length = 120)
    public String descricao;

    /** Nomenclatura Comum do Mercosul: classificação fiscal da mercadoria (8 dígitos). */
    @Column(nullable = false, length = 8)
    public String ncm;

    /** Código Fiscal de Operações e Prestações (ex.: 5102 = venda dentro do estado). */
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
