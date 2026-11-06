package br.pucpr.papelaria.cliente

import jakarta.persistence.*

@Entity
class Cliente(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var nome: String = "",

    @Column(nullable = false)
    var email: String = ""
)
