package br.pucpr.papelaria.cliente.responses

import br.pucpr.papelaria.cliente.Cliente

data class ClienteResponse(
    val id: Long,
    val nome: String,
    val email: String
) {
    constructor(cliente: Cliente) : this(
        id = cliente.id!!,
        nome = cliente.nome,
        email = cliente.email
    )
}
