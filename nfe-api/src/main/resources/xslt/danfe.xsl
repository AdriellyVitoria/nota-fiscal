<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:n="http://www.estudo.br/nfe"
                exclude-result-prefixes="n">

    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:decimal-format name="br" decimal-separator="," grouping-separator="."/>

    <xsl:template match="/">
        <html lang="pt-BR">
            <head>
                <meta charset="UTF-8"/>
                <title>DANFE - NF-e <xsl:value-of select="n:NFe/n:infNFe/n:ide/n:nNF"/></title>
                <style>
                    body { font-family: Arial, sans-serif; font-size: 12px; margin: 24px; color: #222; }
                    .danfe { max-width: 820px; margin: auto; border: 1px solid #333; }
                    .bloco { border-bottom: 1px solid #333; padding: 8px 12px; }
                    .titulo { font-size: 10px; text-transform: uppercase; color: #666; }
                    .cabecalho { display: flex; justify-content: space-between; align-items: center; }
                    h1 { font-size: 20px; margin: 0; }
                    .chave { font-family: monospace; font-size: 14px; letter-spacing: 1px; }
                    table { width: 100%; border-collapse: collapse; }
                    th, td { border: 1px solid #999; padding: 4px 6px; }
                    th { background: #eee; font-size: 10px; text-transform: uppercase; }
                    .num { text-align: right; }
                    .total { font-size: 16px; font-weight: bold; text-align: right; }
                    .aviso { color: #b00; font-weight: bold; text-align: center; }
                </style>
            </head>
            <body>
                <xsl:apply-templates select="n:NFe/n:infNFe"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="n:infNFe">
        <div class="danfe">
            <div class="bloco cabecalho">
                <div>
                    <div class="titulo">Emitente</div>
                    <h1><xsl:value-of select="n:emit/n:xNome"/></h1>
                    CNPJ <xsl:call-template name="formatar-cnpj">
                        <xsl:with-param name="cnpj" select="n:emit/n:CNPJ"/>
                    </xsl:call-template>
                    - <xsl:value-of select="n:emit/n:UF"/>
                </div>
                <div>
                    <h1>DANFE</h1>
                    Nº <xsl:value-of select="n:ide/n:nNF"/> - Série <xsl:value-of select="n:ide/n:serie"/><br/>
                    Emissão: <xsl:call-template name="formatar-data">
                        <xsl:with-param name="data" select="n:ide/n:dhEmi"/>
                    </xsl:call-template>
                </div>
            </div>

            <div class="bloco">
                <div class="titulo">Chave de acesso</div>
                <div class="chave">
                    <xsl:call-template name="agrupar">
                        <xsl:with-param name="texto" select="substring(@Id, 4)"/>
                    </xsl:call-template>
                </div>
            </div>

            <div class="bloco">
                <div class="titulo">Destinatário</div>
                <strong><xsl:value-of select="n:dest/n:xNome"/></strong><br/>
                <xsl:choose>
                    <xsl:when test="n:dest/n:CNPJ">
                        CNPJ <xsl:call-template name="formatar-cnpj">
                            <xsl:with-param name="cnpj" select="n:dest/n:CNPJ"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        CPF <xsl:call-template name="formatar-cpf">
                            <xsl:with-param name="cpf" select="n:dest/n:CPF"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
                - <xsl:value-of select="n:dest/n:UF"/>
                <xsl:if test="n:dest/n:email">
                    - <xsl:value-of select="n:dest/n:email"/>
                </xsl:if>
            </div>

            <div class="bloco">
                <div class="titulo">Produtos</div>
                <table>
                    <tr>
                        <th>#</th><th>Código</th><th>Descrição</th><th>NCM</th><th>CFOP</th>
                        <th>Un</th><th>Qtd</th><th>Valor unit.</th><th>Valor total</th>
                    </tr>
                    <xsl:for-each select="n:det">
                        <tr>
                            <td><xsl:value-of select="@nItem"/></td>
                            <td><xsl:value-of select="n:prod/n:cProd"/></td>
                            <td><xsl:value-of select="n:prod/n:xProd"/></td>
                            <td><xsl:value-of select="n:prod/n:NCM"/></td>
                            <td><xsl:value-of select="n:prod/n:CFOP"/></td>
                            <td><xsl:value-of select="n:prod/n:uCom"/></td>
                            <td class="num"><xsl:value-of select="format-number(n:prod/n:qCom, '#.##0,####', 'br')"/></td>
                            <td class="num"><xsl:value-of select="format-number(n:prod/n:vUnCom, '#.##0,00', 'br')"/></td>
                            <td class="num"><xsl:value-of select="format-number(n:prod/n:vProd, '#.##0,00', 'br')"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </div>

            <div class="bloco total">
                Valor total da nota: R$ <xsl:value-of select="format-number(n:total/n:vNF, '#.##0,00', 'br')"/>
            </div>

            <xsl:if test="n:ide/n:tpAmb = '2'">
                <div class="bloco aviso">EMITIDA EM AMBIENTE DE HOMOLOGAÇÃO - SEM VALOR FISCAL</div>
            </xsl:if>
        </div>
    </xsl:template>

    <xsl:template name="agrupar">
        <xsl:param name="texto"/>
        <xsl:value-of select="substring($texto, 1, 4)"/>
        <xsl:if test="string-length($texto) &gt; 4">
            <xsl:text> </xsl:text>
            <xsl:call-template name="agrupar">
                <xsl:with-param name="texto" select="substring($texto, 5)"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template name="formatar-cnpj">
        <xsl:param name="cnpj"/>
        <xsl:value-of select="concat(substring($cnpj,1,2), '.', substring($cnpj,3,3), '.', substring($cnpj,6,3),
                                     '/', substring($cnpj,9,4), '-', substring($cnpj,13,2))"/>
    </xsl:template>

    <xsl:template name="formatar-cpf">
        <xsl:param name="cpf"/>
        <xsl:value-of select="concat(substring($cpf,1,3), '.', substring($cpf,4,3), '.', substring($cpf,7,3),
                                     '-', substring($cpf,10,2))"/>
    </xsl:template>

    <xsl:template name="formatar-data">
        <xsl:param name="data"/>
        <xsl:value-of select="concat(substring($data,9,2), '/', substring($data,6,2), '/', substring($data,1,4),
                                     ' ', substring($data,12,5))"/>
    </xsl:template>

</xsl:stylesheet>
