package br.pucpr.papelaria

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {
    @Autowired
    lateinit var mvc: MockMvc

    lateinit var token: String

    @BeforeEach
    fun login() {
        val body = mvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"usuario":"admin","senha":"admin123"}"""
        }.andExpect { status { isOk() } }.andReturn().response.contentAsString
        token = Regex("\"token\":\"([^\"]+)\"").find(body)!!.groupValues[1]
    }

    private fun postAuth(url: String, json: String): ResultActionsDsl = mvc.post(url) {
        contentType = MediaType.APPLICATION_JSON
        content = json
        header("Authorization", "Bearer $token")
    }

    private fun idDe(r: ResultActionsDsl) =
        Regex("^\\{\"id\":(\\d+)").find(r.andReturn().response.contentAsString)!!.groupValues[1].toLong()

    private fun unico() = UUID.randomUUID().toString().take(8)

    @Test
    fun `senha errada retorna 401`() {
        mvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"usuario":"admin","senha":"errada"}"""
        }.andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `login sem campos retorna 400 com os campos`() {
        mvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.campos.usuario") { exists() }
            jsonPath("$.campos.senha") { exists() }
        }
    }

    @Test
    fun `acoes destrutivas exigem token`() {
        mvc.delete("/produtos/1").andExpect { status { isUnauthorized() } }
        mvc.post("/categorias") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"nome":"Sem token"}"""
        }.andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `GET e publico`() {
        mvc.get("/produtos").andExpect { status { isOk() } }
    }

    @Test
    fun `produto invalido retorna 400 com erro por campo`() {
        postAuth("/produtos", """{"nome":"","preco":0,"categoriaId":-1}""").andExpect {
            status { isBadRequest() }
            jsonPath("$.campos.nome") { exists() }
            jsonPath("$.campos.preco") { exists() }
            jsonPath("$.campos.categoriaId") { exists() }
        }
    }

    @Test
    fun `JSON mal formado retorna 400`() {
        postAuth("/produtos", """{"nome": """).andExpect { status { isBadRequest() } }
    }

    @Test
    fun `filtros invalidos retornam 400`() {
        mvc.get("/produtos?direcao=lado").andExpect { status { isBadRequest() } }
        mvc.get("/produtos?ordenar=senha").andExpect { status { isBadRequest() } }
        mvc.get("/produtos?precoMax=-1").andExpect { status { isBadRequest() } }
        mvc.get("/produtos?precoMin=10&precoMax=5").andExpect { status { isBadRequest() } }
        mvc.get("/produtos/abc").andExpect { status { isBadRequest() } }
    }

    @Test
    fun `filtro por nome e ordenacao`() {
        mvc.get("/produtos?nome=caneta&ordenar=nome&direcao=desc").andExpect {
            status { isOk() }
            jsonPath("$[0].nome") { value("Caneta preta") }
            jsonPath("$[1].nome") { value("Caneta azul") }
        }
    }

    @Test
    fun `categoria duplicada retorna 409`() {
        val nome = "Cat ${unico()}"
        postAuth("/categorias", """{"nome":"$nome"}""").andExpect { status { isCreated() } }
        postAuth("/categorias", """{"nome":"${nome.uppercase()}"}""").andExpect { status { isConflict() } }
    }

    @Test
    fun `email de cliente duplicado retorna 409`() {
        val email = "c${unico()}@teste.com"
        postAuth("/clientes", """{"nome":"Fulano","email":"$email"}""").andExpect { status { isCreated() } }
        postAuth("/clientes", """{"nome":"Outro","email":"$email"}""").andExpect { status { isConflict() } }
        postAuth("/clientes", """{"nome":"Outro","email":"nao-e-email"}""").andExpect { status { isBadRequest() } }
    }

    @Test
    fun `produto, categoria e cliente em uso nao podem ser removidos`() {
        val categoria = idDe(postAuth("/categorias", """{"nome":"Cat ${unico()}"}"""))
        val produto = idDe(postAuth("/produtos", """{"nome":"Lapis","preco":1.50,"categoriaId":$categoria}"""))
        val cliente = idDe(postAuth("/clientes", """{"nome":"Ana","email":"a${unico()}@teste.com"}"""))
        val pedido = idDe(postAuth("/pedidos", """{"clienteId":$cliente}"""))

        postAuth("/pedidos/$pedido/itens", """{"produtoId":$produto,"quantidade":3}""").andExpect {
            status { isOk() }
            jsonPath("$.total") { value(4.5) }
        }
        postAuth("/pedidos/$pedido/itens", """{"produtoId":$produto,"quantidade":0}""")
            .andExpect { status { isBadRequest() } }

        val auth = "Bearer $token"
        mvc.delete("/produtos/$produto") { header("Authorization", auth) }.andExpect { status { isConflict() } }
        mvc.delete("/categorias/$categoria") { header("Authorization", auth) }.andExpect { status { isConflict() } }
        mvc.delete("/clientes/$cliente") { header("Authorization", auth) }.andExpect { status { isConflict() } }

        // apagando o pedido, os itens saem junto e tudo pode ser removido
        mvc.delete("/pedidos/$pedido") { header("Authorization", auth) }.andExpect { status { isNoContent() } }
        mvc.delete("/produtos/$produto") { header("Authorization", auth) }.andExpect { status { isNoContent() } }
        mvc.delete("/categorias/$categoria") { header("Authorization", auth) }.andExpect { status { isNoContent() } }
        mvc.delete("/clientes/$cliente") { header("Authorization", auth) }.andExpect { status { isNoContent() } }
    }

    @Test
    fun `recurso inexistente retorna 404`() {
        mvc.get("/produtos/99999").andExpect { status { isNotFound() } }
        mvc.get("/clientes/99999").andExpect { status { isNotFound() } }
    }
}
