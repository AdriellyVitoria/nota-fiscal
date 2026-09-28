package br.estudo.nfe.xml;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import br.estudo.nfe.cliente.Cliente;
import br.estudo.nfe.config.EmitenteConfig;
import br.estudo.nfe.nota.ItemNota;
import br.estudo.nfe.nota.NotaFiscal;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class NotaXmlMapper {

    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");

    private static final String AMBIENTE_HOMOLOGACAO = "2";

    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");

    @Inject
    EmitenteConfig emitente;

    public NFeXml mapear(NotaFiscal nota) {
        NFeXml.InfNFe inf = new NFeXml.InfNFe();
        inf.id = "NFe" + nota.chaveAcesso;
        inf.ide = ide(nota);
        inf.emit = emit();
        inf.dest = dest(nota.cliente);

        int numeroItem = 1;
        for (ItemNota item : nota.itens) {
            inf.det.add(det(numeroItem++, item));
        }

        inf.total = new NFeXml.Total();
        inf.total.vNF = nota.valorTotal;

        NFeXml nfe = new NFeXml();
        nfe.infNFe = inf;
        return nfe;
    }

    private NFeXml.Ide ide(NotaFiscal nota) {
        NFeXml.Ide ide = new NFeXml.Ide();
        ide.cUF = String.valueOf(emitente.codigoUf());
        ide.mod = "55";
        ide.serie = String.valueOf(nota.serie);
        ide.nNF = String.valueOf(nota.numero);
        ide.dhEmi = nota.dataEmissao.atZoneSameInstant(FUSO_BRASILIA).truncatedTo(ChronoUnit.SECONDS).format(DATA_HORA);
        ide.tpAmb = AMBIENTE_HOMOLOGACAO;
        return ide;
    }

    private NFeXml.Emit emit() {
        NFeXml.Emit emit = new NFeXml.Emit();
        emit.cnpj = emitente.cnpj();
        emit.xNome = emitente.razaoSocial();
        emit.uf = emitente.uf();
        return emit;
    }

    private NFeXml.Dest dest(Cliente cliente) {
        NFeXml.Dest dest = new NFeXml.Dest();
        if (cliente.documento.length() == 14) {
            dest.cnpj = cliente.documento;
        } else {
            dest.cpf = cliente.documento;
        }
        dest.xNome = cliente.nome;
        dest.uf = cliente.uf;
        dest.email = cliente.email;
        return dest;
    }

    private NFeXml.Det det(int numeroItem, ItemNota item) {
        NFeXml.Prod prod = new NFeXml.Prod();
        prod.cProd = item.produto.codigo;
        prod.xProd = item.produto.descricao;
        prod.ncm = item.produto.ncm;
        prod.cfop = item.produto.cfop;
        prod.uCom = item.produto.unidade;
        prod.qCom = semZerosInuteis(item.quantidade);
        prod.vUnCom = item.valorUnitario;
        prod.vProd = item.valorTotal;

        NFeXml.Det det = new NFeXml.Det();
        det.nItem = numeroItem;
        det.prod = prod;
        return det;
    }

    private BigDecimal semZerosInuteis(BigDecimal valor) {
        BigDecimal limpo = valor.stripTrailingZeros();
        return limpo.scale() < 0 ? limpo.setScale(0) : limpo;
    }
}
