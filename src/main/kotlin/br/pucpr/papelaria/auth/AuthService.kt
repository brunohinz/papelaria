package br.pucpr.papelaria.auth

import br.pucpr.papelaria.security.JWT
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    val repository: UsuarioRepository,
    val jwt: JWT
) {
    fun login(usuario: String, senha: String): String {
        val user = repository.findByUsuario(usuario)
        if (user == null || user.senha != senha) {
            log.warn("Login invalido: {}", usuario)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "usuario ou senha invalidos")
        }
        return jwt.createToken(user.usuario)
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuthService::class.java)
    }
}
