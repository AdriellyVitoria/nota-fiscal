package br.estudo.sefaz;

import java.math.BigDecimal;
import java.util.logging.Logger;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = NFeAutorizacao.FILA),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "5")
})
public class ProcessadorAutorizacao implements MessageListener {

    private static final Logger LOG = Logger.getLogger(ProcessadorAutorizacao.class.getName());

    static final BigDecimal LIMITE_HOMOLOGACAO = new BigDecimal("50000.00");
    static final long TEMPO_PROCESSAMENTO_MS = 3000;

    @Inject
    ValidadorXml validador;

    @Inject
    ReciboRepositorio repositorio;

    @Override
    public void onMessage(Message mensagem) {
        try {
            String recibo = mensagem.getStringProperty("recibo");
            String xml = mensagem.getBody(String.class);
            LOG.info(() -> "Processando recibo " + recibo);

            Thread.sleep(TEMPO_PROCESSAMENTO_MS);

            RetornoConsulta resultado = autorizar(validador.extrair(xml));
            repositorio.registrar(recibo, resultado);
            LOG.info(() -> "Recibo " + recibo + " → " + resultado.codigoStatus + " " + resultado.motivo);
        } catch (JMSException e) {
            throw new IllegalStateException("Mensagem JMS inválida", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Processamento interrompido", e);
        }
    }

    private RetornoConsulta autorizar(ValidadorXml.DadosNota nota) {
        if (nota.valorTotal().compareTo(LIMITE_HOMOLOGACAO) > 0) {
            return new RetornoConsulta(CodigoStatus.VALOR_ACIMA_LIMITE,
                    "Rejeição: Valor total acima do limite de homologação (R$ 50.000,00)",
                    nota.chaveAcesso(), null);
        }
        if (!repositorio.marcarAutorizada(nota.chaveAcesso())) {
            return new RetornoConsulta(CodigoStatus.DUPLICIDADE, "Rejeição: Duplicidade de NF-e",
                    nota.chaveAcesso(), null);
        }
        return new RetornoConsulta(CodigoStatus.AUTORIZADO, "Autorizado o uso da NF-e",
                nota.chaveAcesso(), repositorio.novoProtocolo());
    }
}
