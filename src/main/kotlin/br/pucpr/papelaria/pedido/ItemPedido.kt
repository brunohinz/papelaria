package br.pucpr.papelaria.pedido

import br.pucpr.papelaria.produto.Produto
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["pedido_id", "produto_id"])])
class ItemPedido(
    @Id @GeneratedValue
    var id: Long? = null,

    @ManyToOne(optional = false)
    var pedido: Pedido? = null,

    @ManyToOne(optional = false)
    var produto: Produto? = null,

    @Column(nullable = false)
    var quantidade: Int = 1,

    // preco congelado no momento da inclusao no pedido
    @Column(nullable = false, precision = 10, scale = 2)
    var precoUnitario: BigDecimal = BigDecimal.ZERO
)
