package br.pucpr.papelaria.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JWT(@Value("\${jwt.secret}") secret: String) {
    private val chave = Keys.hmacShaKeyFor(secret.toByteArray())

    fun createToken(usuario: String): String = Jwts.builder()
        .subject(usuario)
        .issuedAt(Date())
        .expiration(Date(System.currentTimeMillis() + 3600_000))
        .signWith(chave)
        .compact()

    fun extractUsuario(token: String): String? = try {
        Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).payload.subject
    } catch (e: Exception) {
        null
    }
}
