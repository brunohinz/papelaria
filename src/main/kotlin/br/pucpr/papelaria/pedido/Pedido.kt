package br.pucpr.papelaria.pedido

import br.pucpr.papelaria.cliente.Cliente
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class Pedido(
    @Id @GeneratedValue
    var id: Long? = null,

    @ManyToOne(optional = false)
    var cliente: Cliente? = null,

    @Column(nullable = false)
    var data: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "pedido", cascade = [CascadeType.ALL], orphanRemoval = true)
    var itens: MutableList<ItemPedido> = mutableListOf()
) {
    val total: BigDecimal
        get() = itens.fold(BigDecimal.ZERO) { acc, i -> acc + i.precoUnitario * i.quantidade.toBigDecimal() }
}
