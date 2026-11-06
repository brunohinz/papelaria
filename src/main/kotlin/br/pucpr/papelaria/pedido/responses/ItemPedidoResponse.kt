package br.pucpr.papelaria.pedido.responses

import br.pucpr.papelaria.pedido.ItemPedido
import br.pucpr.papelaria.produto.responses.ProdutoResponse
import java.math.BigDecimal

data class ItemPedidoResponse(
    val id: Long?,
    val produto: ProdutoResponse?,
    val quantidade: Int,
    val precoUnitario: BigDecimal
) {
    constructor(item: ItemPedido) : this(
        id = item.id,
        produto = item.produto?.let { ProdutoResponse(it) },
        quantidade = item.quantidade,
        precoUnitario = item.precoUnitario
    )
}
