package br.pucpr.papelaria.categoria

import br.pucpr.papelaria.produto.Produto
import jakarta.persistence.*

@Entity
class Categoria(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    var nome: String = "",

    @OneToMany(mappedBy = "categoria")
    var produtos: MutableList<Produto> = mutableListOf()
)
