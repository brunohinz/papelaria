package br.pucpr.papelaria.pedido.requests

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class ItemRequest(
    @NotNull
    @Positive
    val produtoId: Long?,

    @NotNull
    @Min(1)
    @Max(1000)
    val quantidade: Int?
)
