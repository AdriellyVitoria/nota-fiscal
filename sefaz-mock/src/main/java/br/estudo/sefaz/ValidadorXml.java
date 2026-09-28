package br.estudo.sefaz;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ValidadorXml {

    static final String NAMESPACE = "http://www.estudo.br/nfe";

    private Schema schema;

    @PostConstruct
    void iniciar() {
        try {
            SchemaFactory fabrica = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            URL xsd = Thread.currentThread().getContextClassLoader().getResource("xsd/nfe-simplificada.xsd");
            schema = fabrica.newSchema(xsd);
        } catch (SAXException e) {
            throw new IllegalStateException("Falha ao carregar o XSD", e);
        }
    }

    public List<String> validar(String xml) {
        List<String> erros = new ArrayList<>();
        try {
            Validator validator = schema.newValidator();
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (SAXException e) {
            erros.add(e.getMessage());
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler XML", e);
        }
        return erros;
    }

    public DadosNota extrair(String xml) {
        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            fabrica.setNamespaceAware(true);
            fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = fabrica.newDocumentBuilder();
            Document documento = builder.parse(new InputSource(new StringReader(xml)));

            Element infNFe = (Element) documento.getElementsByTagNameNS(NAMESPACE, "infNFe").item(0);
            String chave = infNFe.getAttribute("Id").substring(3);
            String valor = documento.getElementsByTagNameNS(NAMESPACE, "vNF").item(0).getTextContent();
            return new DadosNota(chave, new BigDecimal(valor));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException("Falha ao ler dados do XML", e);
        }
    }

    public record DadosNota(String chaveAcesso, BigDecimal valorTotal) {
    }
}
