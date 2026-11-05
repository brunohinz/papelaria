# Backend Papelaria

Trabalho final - Back-End: Web Services (PUCPR)

Sistema de papelaria: categorias de produtos (cadernos, canetas, etc.), clientes e pedidos.

## Equipe
- Bruno Hinz Cristiani
- Icaro Thiago Nunes
- Julia Ferreira Padilha

## Tecnologias
Kotlin, Spring Boot, Spring Data JPA, H2 (em memória), Bean Validation, Spring Security + JWT (jjwt), springdoc-openapi (Swagger UI).

## Como rodar
Precisa de JDK 17 ou mais novo.

```
./gradlew bootRun
```

A API sobe em http://localhost:8080 e o console do H2 em http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:papelaria`).

## Estrutura
Cada pacote tem Entity, Repository, Service, Controller e os DTOs em `requests/` e `responses/`.
```
br.pucpr.papelaria
├── BackendPapelariaApplication.kt
├── BootStrapper.kt            (usuário admin e dados iniciais)
├── GlobalExceptionHandler.kt
├── OpenApiConfig.kt           (Swagger / OpenAPI + JWT Authorize)
├── RootController.kt          (GET /health)
├── auth/        Usuario, UsuarioRepository, AuthService, AuthController, requests/, responses/
├── categoria/   Categoria, CategoriaRepository, CategoriaService, CategoriaController, requests/, responses/
├── produto/     Produto, ProdutoRepository, ProdutoService, ProdutoController, SortDir, requests/, responses/
├── cliente/     Cliente, ClienteRepository, ClienteService, ClienteController, requests/, responses/
├── pedido/      Pedido, ItemPedido, PedidoRepository, PedidoService, PedidoController, requests/, responses/
├── exceptions/  NaoEncontradoException, ProdutoNaoEncontradoException, PedidoInvalidoException
└── security/    JWT, JwtFilter, SecurityConfig
```

## Dados iniciais
O `BootStrapper` cria o usuário `admin`/`admin123` e, se o banco estiver vazio, as categorias Cadernos e Canetas com alguns produtos.

## Classes e relações
- Categoria 1 - N Produto
- Cliente 1 - N Pedido
- Pedido N - N Produto, através de ItemPedido (quantidade, precoUnitario)


## Swagger / OpenAPI
Com a API no ar:
- Swagger UI: http://localhost:8080/swagger-ui/index.html (também em `/swagger-ui.html`)
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Para testar POST/PUT/DELETE no Swagger:
1. Chame `POST /auth/login` com `{"usuario":"admin","senha":"admin123"}` e copie o `token`.
2. Clique em **Authorize**, cole só o JWT (sem a palavra `Bearer`) e confirme.
3. As operações protegidas passam a enviar `Authorization: Bearer <token>`.

## Autenticação
GETs são públicos. POST, PUT e DELETE precisam de token.

```
POST /auth/login  {"usuario":"admin","senha":"admin123"}
```
Usar o token retornado no header `Authorization: Bearer <token>`.

## Endpoints
| Método | Rota | Descrição |
|---|---|---|
| GET | /health | status da API |
| GET | /categorias | lista categorias |
| GET | /categorias/{id} | busca categoria |
| POST | /categorias | cria categoria |
| PUT | /categorias/{id} | atualiza categoria |
| DELETE | /categorias/{id} | remove categoria (sem produtos) |
| GET | /produtos?categoria=&precoMax=&ordenar=&direcao= | lista com filtro e ordenação (ordenar: id, nome, preco; direcao: asc, desc) |
| GET | /produtos/{id} | busca produto |
| POST | /produtos | cria produto |
| PUT | /produtos/{id} | atualiza produto |
| DELETE | /produtos/{id} | remove produto |
| GET | /clientes | lista clientes |
| POST | /clientes | cria cliente |
| GET | /pedidos | lista pedidos |
| GET | /pedidos/{id} | busca pedido |
| POST | /pedidos | cria pedido `{"clienteId":1}` |
| POST | /pedidos/{id}/itens | adiciona produto `{"produtoId":1,"quantidade":2}` |
| DELETE | /pedidos/{id}/itens/{produtoId} | remove produto do pedido |

## Erros
Tratados em `GlobalExceptionHandler` (@RestControllerAdvice): `ProdutoNaoEncontradoException` (404), `PedidoInvalidoException` (400), erros de validação (400).
