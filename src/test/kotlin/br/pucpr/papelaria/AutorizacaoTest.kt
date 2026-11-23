package br.pucpr.papelaria

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class AutorizacaoTest {
    @Autowired
    lateinit var mvc: MockMvc

    private fun tokenDe(usuario: String, senha: String): String {
        val body = mvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"usuario":"$usuario","senha":"$senha"}"""
        }.andExpect { status { isOk() } }.andReturn().response.contentAsString
        return Regex("\"token\":\"([^\"]+)\"").find(body)!!.groupValues[1]
    }

    @Test
    fun `USER recebe 403 no DELETE com o formato do tratamento global`() {
        val user = tokenDe("user", "user123")
        val urls = listOf(
            "/categorias/1",
            "/produtos/1",
            "/clientes/1",
            "/pedidos/1",
            "/pedidos/1/itens/1"
        )

        urls.forEach { url ->
            mvc.delete(url) {
                header("Authorization", "Bearer $user")
            }.andExpect {
                status { isForbidden() }
                jsonPath("$.status") { value(403) }
                jsonPath("$.erro") { value("Acesso negado") }
            }
        }
    }

    @Test
    fun `USER cria e altera, ADMIN remove, e sem token continua 401`() {
        val user = tokenDe("user", "user123")
        val admin = tokenDe("admin", "admin123")
        val nome = "Cat ${UUID.randomUUID().toString().take(8)}"

        mvc.delete("/categorias/1").andExpect { status { isUnauthorized() } }

        val criada = mvc.post("/categorias") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"nome":"$nome"}"""
            header("Authorization", "Bearer $user")
        }.andExpect { status { isCreated() } }.andReturn().response.contentAsString
        val id = Regex("\"id\":(\\d+)").find(criada)!!.groupValues[1]

        mvc.put("/categorias/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"nome":"$nome 2"}"""
            header("Authorization", "Bearer $user")
        }.andExpect {
            status { isOk() }
            jsonPath("$.nome") { value("$nome 2") }
        }

        mvc.delete("/categorias/$id") {
            header("Authorization", "Bearer $user")
        }.andExpect { status { isForbidden() } }

        mvc.get("/categorias/$id").andExpect {
            status { isOk() }
            jsonPath("$.nome") { value("$nome 2") }
        }

        mvc.delete("/categorias/$id") {
            header("Authorization", "Bearer $admin")
        }.andExpect { status { isNoContent() } }

        mvc.get("/categorias/$id").andExpect { status { isNotFound() } }
    }
}
