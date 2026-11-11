package br.pucpr.papelaria.auth

import br.pucpr.papelaria.auth.requests.LoginRequest
import br.pucpr.papelaria.auth.responses.LoginResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    val service: AuthService
) {
    @PostMapping("/login")
    fun login(@Valid @RequestBody login: LoginRequest) =
        ResponseEntity.ok(LoginResponse(service.login(login.usuario!!, login.senha!!)))
}
