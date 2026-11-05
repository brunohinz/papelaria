package br.pucpr.papelaria

import br.pucpr.papelaria.produto.SortDir
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SortDirTest {
    @Test
    fun `find aceita desc sem diferenciar maiusculas`() {
        assertEquals(SortDir.DESC, SortDir.find("DeSc"))
    }

    @Test
    fun `find retorna ASC para outros valores`() {
        assertEquals(SortDir.ASC, SortDir.find("qualquer"))
    }
}
