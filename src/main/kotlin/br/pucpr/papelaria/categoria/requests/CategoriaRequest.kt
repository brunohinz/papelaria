package br.pucpr.papelaria.categoria.requests

import br.pucpr.papelaria.categoria.Categoria
import jakarta.validation.constraints.NotBlank

data class CategoriaRequest(
    @NotBlank
    val nome: String?
) {
    fun toCategoria() = Categoria(nome = nome!!)
}
