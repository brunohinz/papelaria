package br.pucpr.papelaria.auth

import jakarta.persistence.*

@Entity
class Usuario(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(unique = true, nullable = false, length = 50)
    var usuario: String = "",

    // hash BCrypt (60 caracteres), nunca a senha em texto puro
    @Column(nullable = false, length = 100)
    var senha: String = ""
)
