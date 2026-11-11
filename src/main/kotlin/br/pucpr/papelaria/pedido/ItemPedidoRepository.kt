package br.pucpr.papelaria.pedido

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemPedidoRepository : JpaRepository<ItemPedido, Long> {
    fun existsByProdutoId(produtoId: Long): Boolean
}
