# NF-e Estudo

Emissor simplificado de Nota Fiscal Eletrônica, construído para praticar Java, Quarkus, WildFly, SOAP/REST, JMS, Kafka, Infinispan, OAuth, XML/XSD/XSLT, PostgreSQL e Vue.js.

## Estrutura

| Pasta          | Descrição                                   |
|----------------|---------------------------------------------|
| `nfe-api/`     | API principal em Quarkus                    |
| `sefaz-mock/`  | SEFAZ simulado (SOAP + JMS) no WildFly      |
| `nfe-frontend/`| Interface em Vue.js                         |

## Como rodar

```shell
docker compose up -d        # sobe o PostgreSQL
cd nfe-api
./mvnw quarkus:dev          # API em http://localhost:8080 (Dev UI em /q/dev-ui)
```
