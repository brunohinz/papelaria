package br.pucpr.papelaria.cliente

import jakarta.persistence.*

@Entity
class Cliente(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false, length = 100)
    var nome: String = "",

    @Column(nullable = false, unique = true, length = 150)
    var email: String = ""
)
