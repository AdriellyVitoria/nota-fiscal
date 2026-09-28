package br.estudo.nfe.sefaz;

import java.time.temporal.ChronoUnit;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import br.estudo.nfe.sefaz.ws.NFeAutorizacao;
import br.estudo.nfe.sefaz.ws.RetornoConsulta;
import br.estudo.nfe.sefaz.ws.RetornoEnvio;
import io.quarkiverse.cxf.annotation.CXFClient;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SefazClient {

    @CXFClient("sefaz")
    NFeAutorizacao sefaz;

    @Timeout(value = 6, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
    public RespostaSefaz enviar(String xml) {
        RetornoEnvio retorno = sefaz.enviarNFe(xml);
        return new RespostaSefaz(retorno.getCodigoStatus(), retorno.getMotivo(), retorno.getRecibo(), null);
    }

    @Timeout(value = 6, unit = ChronoUnit.SECONDS)
    @Retry(maxRetries = 2, delay = 500)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 10, delayUnit = ChronoUnit.SECONDS)
    public RespostaSefaz consultar(String recibo) {
        RetornoConsulta retorno = sefaz.consultarRecibo(recibo);
        return new RespostaSefaz(retorno.getCodigoStatus(), retorno.getMotivo(), recibo, retorno.getProtocolo());
    }
}
