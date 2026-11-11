package br.pucpr.papelaria.produto

import br.pucpr.papelaria.exceptions.RequisicaoInvalidaException

enum class SortDir {
    ASC, DESC;

    companion object {
        // aceita asc/desc sem diferenciar maiusculas; qualquer outro valor e erro 400
        fun find(direcao: String): SortDir =
            entries.firstOrNull { it.name.equals(direcao.trim(), ignoreCase = true) }
                ?: throw RequisicaoInvalidaException("direcao deve ser asc ou desc")
    }
}
