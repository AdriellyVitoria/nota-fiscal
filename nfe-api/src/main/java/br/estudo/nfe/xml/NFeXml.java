package br.estudo.nfe.xml;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "NFe")
@XmlAccessorType(XmlAccessType.FIELD)
public class NFeXml {

    public static final String NAMESPACE = "http://www.estudo.br/nfe";

    @XmlElement(required = true)
    public InfNFe infNFe;

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = { "ide", "emit", "dest", "det", "total" })
    public static class InfNFe {

        @XmlAttribute(name = "versao", required = true)
        public String versao = "1.00";

        @XmlAttribute(name = "Id", required = true)
        public String id;

        @XmlElement(required = true)
        public Ide ide;

        @XmlElement(required = true)
        public Emit emit;

        @XmlElement(required = true)
        public Dest dest;

        @XmlElement(required = true)
        public List<Det> det = new ArrayList<>();

        @XmlElement(required = true)
        public Total total;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = { "cUF", "mod", "serie", "nNF", "dhEmi", "tpAmb" })
    public static class Ide {
        @XmlElement(name = "cUF") public String cUF;
        @XmlElement(name = "mod") public String mod;
        @XmlElement(name = "serie") public String serie;
        @XmlElement(name = "nNF") public String nNF;
        @XmlElement(name = "dhEmi") public String dhEmi;
        @XmlElement(name = "tpAmb") public String tpAmb;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = { "cnpj", "xNome", "uf" })
    public static class Emit {
        @XmlElement(name = "CNPJ") public String cnpj;
        @XmlElement(name = "xNome") public String xNome;
        @XmlElement(name = "UF") public String uf;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = { "cnpj", "cpf", "xNome", "uf", "email" })
    public static class Dest {
        @XmlElement(name = "CNPJ") public String cnpj;
        @XmlElement(name = "CPF") public String cpf;
        @XmlElement(name = "xNome") public String xNome;
        @XmlElement(name = "UF") public String uf;
        @XmlElement(name = "email") public String email;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Det {
        @XmlAttribute(name = "nItem", required = true) public int nItem;
        @XmlElement(required = true) public Prod prod;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = { "cProd", "xProd", "ncm", "cfop", "uCom", "qCom", "vUnCom", "vProd" })
    public static class Prod {
        @XmlElement(name = "cProd") public String cProd;
        @XmlElement(name = "xProd") public String xProd;
        @XmlElement(name = "NCM") public String ncm;
        @XmlElement(name = "CFOP") public String cfop;
        @XmlElement(name = "uCom") public String uCom;
        @XmlElement(name = "qCom") public BigDecimal qCom;
        @XmlElement(name = "vUnCom") public BigDecimal vUnCom;
        @XmlElement(name = "vProd") public BigDecimal vProd;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Total {
        @XmlElement(name = "vNF") public BigDecimal vNF;
    }
}
