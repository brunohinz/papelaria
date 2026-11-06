package br.pucpr.papelaria.pedido

import br.pucpr.papelaria.produto.Produto
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class ItemPedido(
    @Id @GeneratedValue
    var id: Long? = null,

    @ManyToOne
    var pedido: Pedido? = null,

    @ManyToOne
    var produto: Produto? = null,

    var quantidade: Int = 1,

    var precoUnitario: BigDecimal = BigDecimal.ZERO
)
