package br.estudo.sefaz;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class RetornoConsulta {

    public String codigoStatus;
    public String motivo;
    public String chaveAcesso;
    public String protocolo;

    public RetornoConsulta() {
    }

    public RetornoConsulta(String codigoStatus, String motivo, String chaveAcesso, String protocolo) {
        this.codigoStatus = codigoStatus;
        this.motivo = motivo;
        this.chaveAcesso = chaveAcesso;
        this.protocolo = protocolo;
    }
}
