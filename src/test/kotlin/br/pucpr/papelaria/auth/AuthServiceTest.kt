package br.pucpr.papelaria.auth

import br.pucpr.papelaria.security.JWT
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertEquals

class AuthServiceTest {
    private val repository = mockk<UsuarioRepository>()
    private val jwt = JWT("0123456789abcdef0123456789abcdef")
    private val encoder = BCryptPasswordEncoder()
    private val service = AuthService(repository, jwt, encoder)

    @Test
    fun `login devolve token com usuario e papel`() {
        every { repository.findByUsuario("user") } returns Usuario(
            usuario = "user",
            senha = encoder.encode("user123")!!,
            role = Role.USER
        )

        val token = service.login("user", "user123")

        assertEquals("user", jwt.extractUsuario(token))
        assertEquals("USER", jwt.extractRole(token))
    }

    @Test
    fun `login de admin coloca papel ADMIN no token`() {
        every { repository.findByUsuario("admin") } returns Usuario(
            usuario = "admin",
            senha = encoder.encode("admin123")!!,
            role = Role.ADMIN
        )

        val token = service.login("admin", "admin123")

        assertEquals("admin", jwt.extractUsuario(token))
        assertEquals("ADMIN", jwt.extractRole(token))
    }

    @Test
    fun `usuario inexistente retorna 401`() {
        every { repository.findByUsuario("ninguem") } returns null

        val ex = assertThrows<ResponseStatusException> { service.login("ninguem", "x") }

        assertEquals(HttpStatus.UNAUTHORIZED, ex.statusCode)
    }

    @Test
    fun `senha errada retorna 401`() {
        every { repository.findByUsuario("user") } returns Usuario(
            usuario = "user",
            senha = encoder.encode("user123")!!,
            role = Role.USER
        )

        val ex = assertThrows<ResponseStatusException> { service.login("user", "errada") }

        assertEquals(HttpStatus.UNAUTHORIZED, ex.statusCode)
    }
}
