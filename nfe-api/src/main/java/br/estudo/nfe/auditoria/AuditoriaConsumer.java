package br.estudo.nfe.auditoria;

import java.time.OffsetDateTime;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.estudo.nfe.evento.NotaAutorizadaEvento;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuditoriaConsumer {

    private static final Logger LOG = Logger.getLogger(AuditoriaConsumer.class);

    @Inject
    ObjectMapper json;

    @Incoming("auditoria-nfe")
    @Blocking
    @Transactional
    public void consumir(ConsumerRecord<String, String> registro) throws JsonProcessingException {
        NotaAutorizadaEvento evento = json.readValue(registro.value(), NotaAutorizadaEvento.class);

        if (AuditoriaNfe.jaRegistrada(evento.chaveAcesso())) {
            LOG.infof("Evento repetido ignorado: %s (partição %d, offset %d)",
                    evento.chaveAcesso(), registro.partition(), registro.offset());
            return;
        }

        AuditoriaNfe auditoria = new AuditoriaNfe();
        auditoria.chaveAcesso = evento.chaveAcesso();
        auditoria.numero = evento.numero();
        auditoria.protocolo = evento.protocolo();
        auditoria.clienteNome = evento.clienteNome();
        auditoria.valorTotal = evento.valorTotal();
        auditoria.autorizadaEm = evento.autorizadaEm();
        auditoria.registradaEm = OffsetDateTime.now();
        auditoria.particao = registro.partition();
        auditoria.offsetKafka = registro.offset();
        auditoria.persist();

        LOG.infof("Auditoria registrada: nota %d (partição %d, offset %d)",
                evento.numero(), registro.partition(), registro.offset());
    }
}
