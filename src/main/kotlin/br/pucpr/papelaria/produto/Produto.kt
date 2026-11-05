package br.pucpr.papelaria.produto

import br.pucpr.papelaria.categoria.Categoria
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class Produto(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var nome: String = "",

    var descricao: String? = null,

    var preco: BigDecimal = BigDecimal.ZERO,

    @ManyToOne
    var categoria: Categoria? = null
)
