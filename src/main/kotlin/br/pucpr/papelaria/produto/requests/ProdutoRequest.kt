package br.pucpr.papelaria.produto.requests

import br.pucpr.papelaria.produto.Produto
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class ProdutoRequest(
    @NotBlank
    val nome: String?,

    val descricao: String?,

    @NotNull
    @DecimalMin("0.01")
    val preco: BigDecimal?,

    @NotNull
    val categoriaId: Long?
) {
    fun toProduto() = Produto(
        nome = nome!!,
        descricao = descricao,
        preco = preco!!
    )
}
