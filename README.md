# Pedidos API

API HTTP para cadastro de clientes e produtos e consulta de pedidos.

## Requisitos

- JDK 25
- Nenhuma instalação extra do Gradle: use o wrapper do repositório

## Executar

```bash
./gradlew bootRun
```

A API fica em [http://localhost:8080](http://localhost:8080). Na primeira subida, o Flyway cria o banco em `data/pedidos.db` e o `data.sql` carrega clientes e produtos de exemplo.

## Documentação

| Recurso | Endereço |
| --- | --- |
| Scalar | [http://localhost:8080/scalar](http://localhost:8080/scalar) |
| OpenAPI JSON | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |

## Endpoints

| Método | Caminho | Efeito |
| --- | --- | --- |
| `GET` | `/clientes` | Lista os clientes |
| `GET` | `/clientes/{id}` | Busca um cliente |
| `POST` | `/clientes` | Cria um cliente |
| `PUT` | `/clientes/{id}` | Atualiza um cliente |
| `DELETE` | `/clientes/{id}` | Remove um cliente |
| `GET` | `/produtos` | Lista os produtos |
| `GET` | `/produtos/{id}` | Busca um produto |
| `POST` | `/produtos` | Cria um produto |
| `PUT` | `/produtos/{id}` | Atualiza um produto |
| `DELETE` | `/produtos/{id}` | Remove um produto |
| `GET` | `/pedidos` | Lista os pedidos, com o nome do cliente e os itens |
| `GET` | `/pedidos/{id}` | Lista o pedido desse id, ou uma lista vazia |
| `POST` | `/pedidos` | Cria um pedido e retorna o pedido completo com seus itens |

No `POST /pedidos`, envie `clienteId` e `itens`, cada um com `produtoId` e `quantidade`. Em caso de sucesso, a resposta HTTP `200` contém `id`, `cliente` (nome), `status`, `total`, `dataCriacao` e `itens` com `id`, `produto` (nome) e `quantidade`.

No `POST /clientes`, `id` e `dataCadastro` são opcionais. Se faltarem, a API gera um UUID e a data atual. `vip`, `corporativo` e `bloqueado` omitidos viram `false`.

```bash
curl -s http://localhost:8080/clientes
```

## Erros

Respostas de erro usam `application/problem+json` (RFC 9457).

- `404` quando o id não existe
- `409` quando o e-mail do cliente já está cadastrado

## Testes

```bash
./gradlew test
```

### Testes manuais com Bruno

A coleção fica em `bruno/` e usa o ambiente `Local` (`http://localhost:8080`).

```bash
cd bruno
bru run --env Local -r
```

No app do Bruno, abra a pasta `bruno/` e selecione o ambiente `Local`. Rode `Clientes` antes de `Produtos` e `Pedidos`, porque as criações passam os ids adiante pelas variáveis da coleção.

## Persistência

- Migrações do Flyway em `src/main/resources/db/migration`
- Carga inicial em `src/main/resources/data.sql`, com `INSERT OR IGNORE`
- O arquivo `data/*.db` fica só na máquina local e não entra no Git
