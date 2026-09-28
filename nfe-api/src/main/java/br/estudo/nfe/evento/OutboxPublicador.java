package br.estudo.nfe.evento;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OutboxPublicador {

    private static final Logger LOG = Logger.getLogger(OutboxPublicador.class);

    private static final int LOTE = 100;
    private static final long TIMEOUT_ACK_SEGUNDOS = 10;

    @Inject
    @Channel("nfe-autorizada")
    Emitter<Record<String, String>> kafka;

    @Scheduled(every = "{nfe.outbox.intervalo}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void agendado() {
        publicarPendentes();
    }

    public int publicarPendentes() {
        List<Pendente> pendentes = QuarkusTransaction.requiringNew().call(() -> EventoOutbox.pendentes(LOTE)
                .stream()
                .map(e -> new Pendente(e.id, e.chave, e.payload))
                .toList());

        int publicados = 0;
        for (Pendente evento : pendentes) {
            try {
                kafka.send(Record.of(evento.chave(), evento.payload()))
                        .toCompletableFuture()
                        .get(TIMEOUT_ACK_SEGUNDOS, TimeUnit.SECONDS);
            } catch (ExecutionException | TimeoutException e) {
                LOG.warnf("Kafka indisponível, evento %d fica pendente: %s", evento.id(), e.toString());
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            QuarkusTransaction.requiringNew().run(() -> marcarPublicado(evento.id()));
            publicados++;
        }
        if (publicados > 0) {
            LOG.infof("%d evento(s) publicados no Kafka", publicados);
        }
        return publicados;
    }

    private void marcarPublicado(Long id) {
        EventoOutbox evento = EventoOutbox.findById(id);
        evento.publicadoEm = OffsetDateTime.now();
    }

    private record Pendente(Long id, String chave, String payload) {
    }
}
