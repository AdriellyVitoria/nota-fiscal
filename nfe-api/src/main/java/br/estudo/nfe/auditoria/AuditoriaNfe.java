package br.estudo.nfe.auditoria;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria_nfe")
public class AuditoriaNfe extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "chave_acesso", nullable = false, unique = true, length = 44)
    public String chaveAcesso;

    @Column(nullable = false)
    public Long numero;

    @Column(nullable = false, length = 20)
    public String protocolo;

    @Column(name = "cliente_nome", nullable = false, length = 120)
    public String clienteNome;

    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    public BigDecimal valorTotal;

    @Column(name = "autorizada_em", nullable = false)
    public OffsetDateTime autorizadaEm;

    @Column(name = "registrada_em", nullable = false)
    public OffsetDateTime registradaEm;

    @Column(nullable = false)
    public Integer particao;

    @Column(name = "offset_kafka", nullable = false)
    public Long offsetKafka;

    public static boolean jaRegistrada(String chaveAcesso) {
        return count("chaveAcesso", chaveAcesso) > 0;
    }
}
