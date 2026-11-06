package br.pucpr.papelaria.pedido.requests

import jakarta.validation.constraints.NotNull

data class PedidoRequest(
    @NotNull
    val clienteId: Long?
)
