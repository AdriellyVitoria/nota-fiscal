package br.estudo.nfe.evento;

import java.time.OffsetDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "evento_outbox")
public class EventoOutbox extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 60)
    public String tipo;

    @Column(nullable = false, length = 100)
    public String chave;

    @Column(nullable = false, columnDefinition = "text")
    public String payload;

    @Column(name = "criado_em", nullable = false)
    public OffsetDateTime criadoEm;

    @Column(name = "publicado_em")
    public OffsetDateTime publicadoEm;

    public static List<EventoOutbox> pendentes(int limite) {
        return find("publicadoEm is null", Sort.by("id"))
                .page(Page.ofSize(limite))
                .list();
    }

    public static long contarPendentes() {
        return count("publicadoEm is null");
    }
}
