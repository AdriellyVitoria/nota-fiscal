package br.estudo.nfe.cliente;

public record ClienteResponse(Long id, String documento, String nome, String uf, String email) {

    public static ClienteResponse de(Cliente c) {
        return new ClienteResponse(c.id, c.documento, c.nome, c.uf, c.email);
    }
}
