package br.pucpr.papelaria.produto.requests

import br.pucpr.papelaria.produto.Produto
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class ProdutoRequest(
    @NotBlank
    @Size(min = 2, max = 100)
    val nome: String?,

    @Size(max = 500)
    val descricao: String?,

    @NotNull
    @DecimalMin("0.01")
    @DecimalMax("99999.99")
    @Digits(integer = 5, fraction = 2)
    val preco: BigDecimal?,

    @NotNull
    @Positive
    val categoriaId: Long?
) {
    fun toProduto() = Produto(
        nome = nome!!.trim(),
        descricao = descricao?.trim()?.ifEmpty { null },
        preco = preco!!
    )
}
