package br.estudo.nfe.evento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import br.estudo.nfe.nota.NotaFiscal;

public record NotaAutorizadaEvento(
        String chaveAcesso,
        Long numero,
        Integer serie,
        String protocolo,
        String clienteDocumento,
        String clienteNome,
        BigDecimal valorTotal,
        OffsetDateTime autorizadaEm) {

    public static final String TIPO = "NotaAutorizada";

    public static NotaAutorizadaEvento de(NotaFiscal nota, OffsetDateTime autorizadaEm) {
        return new NotaAutorizadaEvento(nota.chaveAcesso, nota.numero, nota.serie, nota.protocolo,
                nota.cliente.documento, nota.cliente.nome, nota.valorTotal, autorizadaEm);
    }
}
