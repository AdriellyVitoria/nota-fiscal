package br.estudo.nfe.xml;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class XmlServiceTest {

    private XmlService service;

    @BeforeEach
    void preparar() {
        service = new XmlService();
        service.iniciar();
    }

    @Test
    void xmlGeradoPeloJaxbPassaNoXsd() {
        String xml = service.gerar(notaValida());

        List<String> erros = service.validar(xml);

        assertTrue(erros.isEmpty(), () -> "Esperava XML válido, mas veio: " + erros);
        assertTrue(xml.contains("<NFe xmlns=\"http://www.estudo.br/nfe\">"));
    }

    @Test
    void cnpjComMenosDigitosEhRejeitado() {
        NFeXml nfe = notaValida();
        nfe.infNFe.emit.cnpj = "1122233300018";

        List<String> erros = service.validar(service.gerar(nfe));

        assertFalse(erros.isEmpty());
    }

    @Test
    void notaSemItensEhRejeitada() {
        NFeXml nfe = notaValida();
        nfe.infNFe.det.clear();

        assertFalse(service.validar(service.gerar(nfe)).isEmpty());
    }

    @Test
    void xmlMalFormadoEhRejeitadoSemExcecao() {
        assertFalse(service.validar("<NFe><infNFe>").isEmpty());
    }

    private NFeXml notaValida() {
        NFeXml.Ide ide = new NFeXml.Ide();
        ide.cUF = "35";
        ide.mod = "55";
        ide.serie = "1";
        ide.nNF = "10";
        ide.dhEmi = "2026-09-28T10:30:00-03:00";
        ide.tpAmb = "2";

        NFeXml.Emit emit = new NFeXml.Emit();
        emit.cnpj = "11222333000181";
        emit.xNome = "Loja Estudo LTDA";
        emit.uf = "SP";

        NFeXml.Dest dest = new NFeXml.Dest();
        dest.cpf = "12345678909";
        dest.xNome = "Maria Silva";
        dest.uf = "SP";

        NFeXml.Prod prod = new NFeXml.Prod();
        prod.cProd = "P002";
        prod.xProd = "Mouse sem fio";
        prod.ncm = "84716053";
        prod.cfop = "5102";
        prod.uCom = "UN";
        prod.qCom = new java.math.BigDecimal("2");
        prod.vUnCom = new java.math.BigDecimal("80.50");
        prod.vProd = new java.math.BigDecimal("161.00");

        NFeXml.Det det = new NFeXml.Det();
        det.nItem = 1;
        det.prod = prod;

        NFeXml.Total total = new NFeXml.Total();
        total.vNF = new java.math.BigDecimal("161.00");

        NFeXml.InfNFe inf = new NFeXml.InfNFe();
        inf.id = "NFe35260911222333000181550010000000101123456780";
        inf.ide = ide;
        inf.emit = emit;
        inf.dest = dest;
        inf.det.add(det);
        inf.total = total;

        NFeXml nfe = new NFeXml();
        nfe.infNFe = inf;
        return nfe;
    }
}
