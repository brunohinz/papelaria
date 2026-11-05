package br.pucpr.papelaria.categoria.responses

import br.pucpr.papelaria.categoria.Categoria

data class CategoriaResponse(
    val id: Long,
    val nome: String
) {
    constructor(categoria: Categoria) : this(
        id = categoria.id!!,
        nome = categoria.nome
    )
}
