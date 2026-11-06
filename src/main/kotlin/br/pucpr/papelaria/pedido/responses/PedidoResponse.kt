package br.pucpr.papelaria.pedido.responses

import br.pucpr.papelaria.cliente.responses.ClienteResponse
import br.pucpr.papelaria.pedido.Pedido
import java.math.BigDecimal
import java.time.LocalDateTime

data class PedidoResponse(
    val id: Long,
    val cliente: ClienteResponse?,
    val data: LocalDateTime,
    val itens: List<ItemPedidoResponse>,
    val total: BigDecimal
) {
    constructor(pedido: Pedido) : this(
        id = pedido.id!!,
        cliente = pedido.cliente?.let { ClienteResponse(it) },
        data = pedido.data,
        itens = pedido.itens.map { ItemPedidoResponse(it) },
        total = pedido.total
    )
}
