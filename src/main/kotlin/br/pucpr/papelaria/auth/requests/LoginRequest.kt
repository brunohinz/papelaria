package br.pucpr.papelaria.auth.requests

data class LoginRequest(
    val usuario: String,
    val senha: String
)
