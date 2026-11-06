package br.pucpr.papelaria.cliente.requests

import br.pucpr.papelaria.cliente.Cliente
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ClienteRequest(
    @NotBlank
    val nome: String?,

    @NotBlank
    @Email
    val email: String?
) {
    fun toCliente() = Cliente(nome = nome!!, email = email!!)
}
