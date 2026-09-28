package br.estudo.sefaz;

import java.util.List;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSDestinationDefinition;
import jakarta.jms.Queue;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@JMSDestinationDefinition(
        name = NFeAutorizacao.FILA,
        interfaceName = "jakarta.jms.Queue",
        destinationName = "NFeAutorizacao")
@WebService(
        name = "NFeAutorizacao",
        serviceName = "NFeAutorizacao",
        portName = "NFeAutorizacaoPort",
        targetNamespace = "http://www.estudo.br/sefaz")
public class NFeAutorizacao {

    static final String FILA = "java:global/jms/queue/NFeAutorizacao";

    @Inject
    ValidadorXml validador;

    @Inject
    ReciboRepositorio repositorio;

    @Inject
    JMSContext jms;

    @Resource(lookup = FILA)
    Queue fila;

    @WebMethod
    @WebResult(name = "retorno")
    public RetornoEnvio enviarNFe(@WebParam(name = "xml") String xml) {
        List<String> erros = validador.validar(xml);
        if (!erros.isEmpty()) {
            return new RetornoEnvio(CodigoStatus.FALHA_SCHEMA,
                    "Rejeição: Falha no Schema XML - " + erros.get(0), null);
        }

        String recibo = repositorio.novoRecibo();
        jms.createProducer()
                .setProperty("recibo", recibo)
                .send(fila, xml);

        return new RetornoEnvio(CodigoStatus.LOTE_RECEBIDO, "Lote recebido com sucesso", recibo);
    }

    @WebMethod
    @WebResult(name = "retorno")
    public RetornoConsulta consultarRecibo(@WebParam(name = "recibo") String recibo) {
        return repositorio.buscar(recibo)
                .orElse(new RetornoConsulta(CodigoStatus.RECIBO_INEXISTENTE,
                        "Rejeição: Número do Recibo não encontrado", null, null));
    }
}
