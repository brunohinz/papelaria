package br.pucpr.papelaria.auth

import br.pucpr.papelaria.security.JWT
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    val repository: UsuarioRepository,
    val jwt: JWT,
    val passwordEncoder: PasswordEncoder
) {
    fun login(usuario: String, senha: String): String {
        val user = repository.findByUsuario(usuario)
        // a senha e guardada com hash BCrypt; matches compara a senha digitada com o hash
        if (user == null || !passwordEncoder.matches(senha, user.senha)) {
            log.warn("Login invalido: {}", usuario)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "usuario ou senha invalidos")
        }
        log.info("Login realizado: {}", usuario)
        return jwt.createToken(user.usuario)
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuthService::class.java)
    }
}
