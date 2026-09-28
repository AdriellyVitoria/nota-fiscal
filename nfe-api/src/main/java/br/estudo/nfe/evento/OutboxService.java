package br.estudo.nfe.evento;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class OutboxService {

    @Inject
    ObjectMapper json;

    @Transactional(TxType.MANDATORY)
    public void registrar(String tipo, String chave, Object evento) {
        EventoOutbox registro = new EventoOutbox();
        registro.tipo = tipo;
        registro.chave = chave;
        registro.payload = serializar(evento);
        registro.criadoEm = OffsetDateTime.now();
        registro.persist();
    }

    private String serializar(Object evento) {
        try {
            return json.writeValueAsString(evento);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao serializar evento " + evento, e);
        }
    }
}
