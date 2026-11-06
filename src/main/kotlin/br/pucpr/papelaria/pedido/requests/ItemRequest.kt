package br.pucpr.papelaria.pedido.requests

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class ItemRequest(
    @NotNull
    val produtoId: Long?,

    @NotNull
    @Min(1)
    val quantidade: Int?
)
