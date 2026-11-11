package br.pucpr.papelaria.pedido.requests

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class PedidoRequest(
    @NotNull
    @Positive
    val clienteId: Long?
)
