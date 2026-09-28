package br.estudo.nfe.config;

import io.smallrye.config.ConfigMapping;

/**
 * Dados da empresa emitente, lidos do application.properties (prefixo "nfe.emitente").
 * O Quarkus valida na subida: se faltar alguma propriedade, a aplicação não inicia.
 */
@ConfigMapping(prefix = "nfe.emitente")
public interface EmitenteConfig {

    String cnpj();

    String razaoSocial();

    String uf();

    /** Código IBGE da UF (ex.: 35 = SP), usado na chave de acesso. */
    int codigoUf();

    int serie();
}
