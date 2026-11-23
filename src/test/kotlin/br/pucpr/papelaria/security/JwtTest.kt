package br.pucpr.papelaria.security

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JwtTest {
    private val jwt = JWT("0123456789abcdef0123456789abcdef")

    @Test
    fun `token guarda usuario e papel`() {
        val token = jwt.createToken("admin", "ADMIN")

        assertEquals("admin", jwt.extractUsuario(token))
        assertEquals("ADMIN", jwt.extractRole(token))
    }

    @Test
    fun `token invalido nao autentica`() {
        assertNull(jwt.extractUsuario("nao-e-um-jwt"))
        assertNull(jwt.extractRole("nao-e-um-jwt"))
        assertNull(jwt.extractUsuario(""))
    }
}
