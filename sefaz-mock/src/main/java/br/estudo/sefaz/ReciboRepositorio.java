package br.estudo.sefaz;

import java.time.Year;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReciboRepositorio {

    private final Map<String, RetornoConsulta> resultados = new ConcurrentHashMap<>();
    private final Set<String> chavesAutorizadas = ConcurrentHashMap.newKeySet();
    private final AtomicLong sequenciaRecibo = new AtomicLong();
    private final AtomicLong sequenciaProtocolo = new AtomicLong();

    public String novoRecibo() {
        String recibo = String.format("35%013d", sequenciaRecibo.incrementAndGet());
        resultados.put(recibo, new RetornoConsulta(CodigoStatus.LOTE_EM_PROCESSAMENTO,
                "Lote em processamento", null, null));
        return recibo;
    }

    public String novoProtocolo() {
        int ano = Year.now().getValue() % 100;
        return String.format("135%02d%010d", ano, sequenciaProtocolo.incrementAndGet());
    }

    public boolean marcarAutorizada(String chaveAcesso) {
        return chavesAutorizadas.add(chaveAcesso);
    }

    public void registrar(String recibo, RetornoConsulta resultado) {
        resultados.put(recibo, resultado);
    }

    public Optional<RetornoConsulta> buscar(String recibo) {
        return Optional.ofNullable(resultados.get(recibo));
    }
}
