package br.estudo.nfe.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "nfe.emitente")
public interface EmitenteConfig {

    String cnpj();

    String razaoSocial();

    String uf();

    int codigoUf();

    int serie();
}
