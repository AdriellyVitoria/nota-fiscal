# NF-e Estudo

Emissor simplificado de Nota Fiscal Eletrônica, construído para praticar Java, Quarkus, WildFly, SOAP/REST, JMS, Kafka, Infinispan, OAuth, XML/XSD/XSLT, PostgreSQL e Vue.js.

## Estrutura

| Pasta           | Descrição                                            |
|-----------------|------------------------------------------------------|
| `nfe-api/`      | API principal em Quarkus                             |
| `sefaz-mock/`   | SEFAZ simulado no WildFly (SOAP + fila JMS + MDB)    |
| `nfe-frontend/` | Interface em Vue.js                                  |

## Como rodar

```shell
docker compose up -d --build   # PostgreSQL (5433), SEFAZ no WildFly (8180), Kafka (9092)
cd nfe-api
./mvnw quarkus:dev             # API em http://localhost:8081
```

| Endereço | O quê |
|---|---|
| http://localhost:8081/q/swagger-ui | Swagger da API |
| http://localhost:8180/sefaz-mock/NFeAutorizacao?wsdl | WSDL do SEFAZ simulado |
| http://localhost:9991 | Console de administração do WildFly |

## Fluxo de emissão

1. `POST /notas` cria a nota em `RASCUNHO`
2. `POST /notas/{id}/emitir` gera o XML, valida no XSD e envia via SOAP → `ENVIADA` (202)
3. O SEFAZ processa pela fila JMS e a API consulta o recibo a cada 5s → `AUTORIZADA` ou `REJEITADA`
4. Ao autorizar, o evento é gravado no outbox e publicado no tópico Kafka `nfe-autorizada` (3 partições)
5. O consumidor de auditoria registra o evento — `GET /auditoria`
6. `GET /notas/{id}/danfe` mostra o DANFE
