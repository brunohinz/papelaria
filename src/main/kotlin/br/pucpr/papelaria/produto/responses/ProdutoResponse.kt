package br.pucpr.papelaria.produto.responses

import br.pucpr.papelaria.categoria.responses.CategoriaResponse
import br.pucpr.papelaria.produto.Produto
import java.math.BigDecimal

data class ProdutoResponse(
    val id: Long,
    val nome: String,
    val descricao: String?,
    val preco: BigDecimal,
    val categoria: CategoriaResponse?
) {
    constructor(produto: Produto) : this(
        id = produto.id!!,
        nome = produto.nome,
        descricao = produto.descricao,
        preco = produto.preco,
        categoria = produto.categoria?.let { CategoriaResponse(it) }
    )
}
