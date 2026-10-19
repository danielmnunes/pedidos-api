# AGENTS.md

## Projeto

API Spring Boot 4.1 com Java 25. Cadastro de clientes e produtos via JDBC (`JdbcClient`) e SQLite. Sem JPA.

As tabelas `pedidos` e `itens_pedido` existem em `src/main/resources/db/migration/V1__criar_tabelas.sql` e não têm controller nem repositório. Não crie essa API sem um pedido explícito.

O uso da API para pessoas está no `README.md`. Não copie essa documentação para cá.

## Comandos

| Tarefa | Comando |
| --- | --- |
| Testes | `./gradlew test` |
| Um teste | `./gradlew test --tests com.dev.pedidos_api.CrudControllersTest` |
| Subir a API | `./gradlew bootRun` |

A porta padrão é `8080`. A referência interativa é `/scalar` e o OpenAPI JSON é `/v3/api-docs`.

## Convenções

- Mantenha o pacote único `com.dev.pedidos_api`. Não crie subpacotes.
- Indente com tab, como o código existente.
- Escreva mensagens de erro e textos do OpenAPI em português.
- Modele o domínio com `record` e a persistência com `@Repository`, `JdbcClient` e SQL nomeado.
- Flags de cliente no SQLite são `INTEGER` `0`/`1`.
- A entrada de cliente é o record privado `ClienteBody`, com `Boolean`. A resposta é `Cliente`, com `boolean`. Não una os dois tipos sem pedido.
- Esquema novo vai em `src/main/resources/db/migration/V{n}__descricao.sql`. Não reescreva `V1__criar_tabelas.sql`.
- Dados de exemplo ficam em `src/main/resources/data.sql` e usam `INSERT OR IGNORE`.
- Recurso ausente passa por `Problemas.naoEncontrado`. Violação de unicidade passa por `ProblemaControllerAdvice`, em `application/problem+json`.
- A UI de documentação é o Scalar (`springdoc-openapi-starter-webmvc-scalar`). Não adicione Swagger UI.

## Testes

- JUnit 5 com `@SpringBootTest`.
- HTTP em `src/test/java/com/dev/pedidos_api/CrudControllersTest.java`, com `MockMvc`.
- Repositórios em `src/test/java/com/dev/pedidos_api/RepositoriosCrudTest.java`.
- Ao mudar um endpoint ou uma consulta, atualize o teste que cobre esse comportamento.

## Arquivos

- Não versione `data/*.db`, `build/` nem `.gradle/`.
- Não altere `gradle/wrapper` sem pedido.
