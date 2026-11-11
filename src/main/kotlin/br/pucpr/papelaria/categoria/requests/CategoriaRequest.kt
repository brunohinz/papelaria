package br.pucpr.papelaria.categoria.requests

import br.pucpr.papelaria.categoria.Categoria
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CategoriaRequest(
    @NotBlank
    @Size(min = 2, max = 50)
    val nome: String?
) {
    fun toCategoria() = Categoria(nome = nome!!.trim())
}
