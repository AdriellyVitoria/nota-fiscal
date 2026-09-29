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
docker compose up -d --build   # PostgreSQL (5433), SEFAZ no WildFly (8180), Kafka (9092), Infinispan (11222), Keycloak (8280)
cd nfe-api
./mvnw quarkus:dev             # API em http://localhost:8081

# em outro terminal
cd nfe-frontend
npm install
npm run dev                    # tela em http://localhost:5173 (login: maria / maria)
```

| Endereço | O quê |
|---|---|
| http://localhost:5173 | **Frontend** (Vue.js) |
| http://localhost:8081/q/swagger-ui | Swagger da API |
| http://localhost:8180/sefaz-mock/NFeAutorizacao?wsdl | WSDL do SEFAZ simulado |
| http://localhost:9991 | Console de administração do WildFly |
| http://localhost:11222 | Console do Infinispan (admin / admin) |
| http://localhost:8280 | Console do Keycloak (admin / admin) — realm `nfe` |

## Fluxo de emissão

1. `POST /notas` cria a nota em `RASCUNHO`
2. `POST /notas/{id}/emitir` gera o XML, valida no XSD e envia via SOAP → `ENVIADA` (202)
3. O SEFAZ processa pela fila JMS e a API consulta o recibo a cada 5s → `AUTORIZADA` ou `REJEITADA`
4. Ao autorizar, o evento é gravado no outbox e publicado no tópico Kafka `nfe-autorizada` (3 partições)
5. O consumidor de auditoria registra o evento — `GET /auditoria`
6. `GET /notas/{id}/danfe` mostra o DANFE
7. `GET /consulta/{chaveAcesso}` consulta o status com cache no Infinispan (header `X-Cache: HIT` ou `MISS`)

## Segurança

A API exige um token JWT emitido pelo Keycloak (realm `nfe`), no header `Authorization: Bearer <token>`.

| Usuário / cliente | Senha / segredo | Papel |
|---|---|---|
| `maria` | `maria` | `emissor` — cadastra, cria e emite notas |
| `joao` | `joao` | `consulta` — só leitura |
| cliente `nfe-integracao` | `nfe-integracao-secret` | `emissor` (client credentials, sistema-a-sistema) |

Token para testes pelo terminal:

```shell
curl -d "grant_type=password&client_id=nfe-dev-cli&username=maria&password=maria" \
  http://localhost:8280/realms/nfe/protocol/openid-connect/token
```
