package br.estudo.sefaz;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class RetornoEnvio {

    public String codigoStatus;
    public String motivo;
    public String recibo;

    public RetornoEnvio() {
    }

    public RetornoEnvio(String codigoStatus, String motivo, String recibo) {
        this.codigoStatus = codigoStatus;
        this.motivo = motivo;
        this.recibo = recibo;
    }
}
