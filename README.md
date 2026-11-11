# Backend Papelaria

Trabalho final - Back-End: Web Services (PUCPR)

Sistema de papelaria: categorias de produtos (cadernos, canetas, etc.), clientes e pedidos.

## Equipe
- Bruno Hinz Cristiani
- Icaro Thiago Nunes
- Julia Ferreira Padilha

## Vídeo
<!-- TODO: colar aqui o link do vídeo no YouTube (pode ser não listado) -->
Link do vídeo: _a adicionar_

## Tecnologias
Kotlin, Spring Boot, Spring Data JPA, H2 (em memória), Bean Validation, Spring Security + JWT (jjwt) com senhas em BCrypt, springdoc-openapi (Swagger UI).

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
├── pedido/      Pedido, ItemPedido, PedidoRepository, ItemPedidoRepository, PedidoService, PedidoController, requests/, responses/
├── exceptions/  NaoEncontradoException, ProdutoNaoEncontradoException, RequisicaoInvalidaException, PedidoInvalidoException, ConflitoException
└── security/    JWT, JwtFilter, SecurityConfig
```

## Dados iniciais
O `BootStrapper` cria o usuário `admin`/`admin123` (senha gravada com hash BCrypt) e, se o banco estiver vazio, as categorias Cadernos e Canetas com alguns produtos.

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
| DELETE | /categorias/{id} | remove categoria (409 se tiver produtos) |
| GET | /produtos?categoria=&nome=&precoMin=&precoMax=&ordenar=&direcao= | lista com filtro e ordenação (ordenar: id, nome, preco; direcao: asc, desc) |
| GET | /produtos/{id} | busca produto |
| POST | /produtos | cria produto |
| PUT | /produtos/{id} | atualiza produto |
| DELETE | /produtos/{id} | remove produto (409 se estiver em algum pedido) |
| GET | /clientes | lista clientes |
| GET | /clientes/{id} | busca cliente |
| POST | /clientes | cria cliente |
| PUT | /clientes/{id} | atualiza cliente |
| DELETE | /clientes/{id} | remove cliente (409 se tiver pedidos) |
| GET | /pedidos | lista pedidos |
| GET | /pedidos/{id} | busca pedido |
| POST | /pedidos | cria pedido `{"clienteId":1}` |
| POST | /pedidos/{id}/itens | adiciona produto `{"produtoId":1,"quantidade":2}` |
| DELETE | /pedidos/{id}/itens/{produtoId} | remove produto do pedido |
| DELETE | /pedidos/{id} | remove pedido (e seus itens) |

Exemplo de consulta: `GET /produtos?nome=caneta&precoMax=10&ordenar=preco&direcao=desc`

## Validações
Feitas com Bean Validation (`@Valid` nos DTOs de `requests/` e anotações nos parâmetros de query):

| Onde | Regras |
|---|---|
| Login | `usuario` e `senha` obrigatórios |
| Categoria | `nome` obrigatório, 2 a 50 caracteres, único (sem diferenciar maiúsculas) |
| Produto | `nome` 2 a 100 caracteres; `descricao` até 500; `preco` entre 0,01 e 99999,99 com no máximo 2 casas decimais; `categoriaId` obrigatório, positivo e existente |
| Cliente | `nome` 2 a 100 caracteres; `email` válido, até 150 caracteres, único |
| Pedido | `clienteId` obrigatório e existente |
| Item do pedido | `produtoId` obrigatório e existente; `quantidade` de 1 a 1000; o mesmo produto não pode entrar duas vezes |
| GET /produtos | `categoria` positivo; `precoMin`/`precoMax` ≥ 0 e `precoMin` ≤ `precoMax`; `nome` até 100; `ordenar` em id/nome/preco; `direcao` asc/desc |

## Erros
Tratados em `GlobalExceptionHandler` (@RestControllerAdvice). Todas as respostas de erro têm o formato
`{"status": 400, "erro": "Dados invalidos", "campos": {"preco": "deve ser maior que ou igual a 0.01"}}` (`campos` só aparece em erros de validação).

| Status | Quando |
|---|---|
| 400 | validação falhou, JSON mal formado, parâmetro com tipo errado (ex.: `/produtos/abc`), filtro inválido, `PedidoInvalidoException` |
| 401 | sem token / token inválido em POST, PUT, DELETE; login com usuário ou senha errados |
| 404 | recurso não encontrado (`NaoEncontradoException`, `ProdutoNaoEncontradoException`) |
| 409 | `ConflitoException`: nome/e-mail duplicado, ou remoção de registro em uso (categoria com produtos, produto em pedido, cliente com pedidos) |
| 500 | erro inesperado (registrado no log com stack trace) |

## Testes
```
./gradlew test
```
`ApiIntegrationTest` cobre autenticação, validações, filtros, conflitos (409) e o fluxo pedido → itens; `SortDirTest` cobre a direção de ordenação.
