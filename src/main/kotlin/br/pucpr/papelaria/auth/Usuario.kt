package br.pucpr.papelaria.auth

import jakarta.persistence.*

@Entity
class Usuario(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var usuario: String = "",

    @Column(nullable = false)
    var senha: String = ""
)
