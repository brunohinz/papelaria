package br.pucpr.papelaria.produto

import br.pucpr.papelaria.categoria.Categoria
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class Produto(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false, length = 100)
    var nome: String = "",

    @Column(length = 500)
    var descricao: String? = null,

    @Column(nullable = false, precision = 10, scale = 2)
    var preco: BigDecimal = BigDecimal.ZERO,

    @ManyToOne(optional = false)
    var categoria: Categoria? = null
)
