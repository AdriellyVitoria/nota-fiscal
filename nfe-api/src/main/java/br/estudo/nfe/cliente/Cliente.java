package br.estudo.nfe.cliente;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Cliente extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 14)
    public String documento;

    @Column(nullable = false, length = 120)
    public String nome;

    @Column(nullable = false, length = 2)
    public String uf;

    @Column(length = 120)
    public String email;

    public static boolean existeDocumento(String documento) {
        return count("documento", documento) > 0;
    }
}
