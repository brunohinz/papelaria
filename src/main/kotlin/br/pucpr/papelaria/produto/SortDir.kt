package br.pucpr.papelaria.produto

enum class SortDir {
    ASC, DESC;

    companion object {
        // igual ao comportamento antigo: qualquer valor diferente de desc vira asc
        fun find(direcao: String): SortDir =
            if (direcao.equals("desc", true)) DESC else ASC
    }
}
