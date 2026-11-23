package br.pucpr.papelaria.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JWT(@Value("\${jwt.secret}") secret: String) {
    private val chave = Keys.hmacShaKeyFor(secret.toByteArray())

    fun createToken(usuario: String, role: String): String = Jwts.builder()
        .subject(usuario)
        .claim("role", role)
        .issuedAt(Date())
        .expiration(Date(System.currentTimeMillis() + 3600_000))
        .signWith(chave)
        .compact()

    fun extractUsuario(token: String): String? = payload(token)?.subject

    fun extractRole(token: String): String? = payload(token)?.get("role", String::class.java)

    private fun payload(token: String) = try {
        Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).payload
    } catch (e: Exception) {
        null
    }
}
