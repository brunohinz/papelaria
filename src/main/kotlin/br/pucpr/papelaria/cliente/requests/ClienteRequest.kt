package br.pucpr.papelaria.cliente.requests

import br.pucpr.papelaria.cliente.Cliente
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ClienteRequest(
    @NotBlank
    @Size(min = 2, max = 100)
    val nome: String?,

    @NotBlank
    @Email
    @Size(max = 150)
    val email: String?
) {
    fun toCliente() = Cliente(nome = nome!!.trim(), email = email!!.trim().lowercase())
}
