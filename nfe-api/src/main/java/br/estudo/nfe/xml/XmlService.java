package br.estudo.nfe.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

@ApplicationScoped
public class XmlService {

    static final String XSD = "xsd/nfe-simplificada.xsd";

    private JAXBContext contexto;
    private Schema schema;

    @PostConstruct
    void iniciar() {
        try {
            contexto = JAXBContext.newInstance(NFeXml.class);

            SchemaFactory fabrica = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            schema = fabrica.newSchema(recurso(XSD));
        } catch (JAXBException | SAXException e) {
            throw new IllegalStateException("Falha ao carregar JAXB/XSD da NF-e", e);
        }
    }

    public String gerar(NFeXml nfe) {
        try {
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            StringWriter saida = new StringWriter();
            marshaller.marshal(nfe, saida);
            return saida.toString();
        } catch (JAXBException e) {
            throw new IllegalStateException("Falha ao gerar XML da NF-e", e);
        }
    }

    public List<String> validar(String xml) {
        List<String> erros = new ArrayList<>();
        try {
            Validator validator = schema.newValidator();
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            validator.setErrorHandler(new ColetorDeErros(erros));
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (SAXException e) {
            if (erros.isEmpty()) {
                erros.add(e.getMessage());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler XML", e);
        }
        return erros;
    }

    private static URL recurso(String caminho) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(caminho);
        if (url == null) {
            throw new IllegalStateException("Arquivo não encontrado no classpath: " + caminho);
        }
        return url;
    }

    private record ColetorDeErros(List<String> erros) implements ErrorHandler {

        @Override
        public void warning(SAXParseException e) {
        }

        @Override
        public void error(SAXParseException e) {
            erros.add(formatar(e));
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            erros.add(formatar(e));
            throw e;
        }

        private static String formatar(SAXParseException e) {
            return "Linha " + e.getLineNumber() + ": " + e.getMessage();
        }
    }
}
