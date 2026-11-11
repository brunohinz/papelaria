package br.pucpr.papelaria.auth.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @NotBlank
    @Size(max = 50)
    val usuario: String?,

    @NotBlank
    @Size(max = 100)
    val senha: String?
)
