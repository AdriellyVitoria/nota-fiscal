package br.estudo.nfe.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DanfeService {

    static final String XSLT = "xslt/danfe.xsl";

    private Templates danfe;

    @PostConstruct
    void iniciar() {
        try {
            TransformerFactory fabrica = TransformerFactory.newInstance();
            fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            URL url = Thread.currentThread().getContextClassLoader().getResource(XSLT);
            if (url == null) {
                throw new IllegalStateException("Arquivo não encontrado no classpath: " + XSLT);
            }
            danfe = fabrica.newTemplates(new StreamSource(url.toExternalForm()));
        } catch (TransformerException e) {
            throw new IllegalStateException("Falha ao compilar o XSLT do DANFE", e);
        }
    }

    public String gerarHtml(String xml) {
        try {
            Transformer transformer = danfe.newTransformer();
            StringWriter html = new StringWriter();
            transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(html));
            return html.toString();
        } catch (TransformerException e) {
            throw new IllegalStateException("Falha ao gerar o DANFE", e);
        }
    }
}
