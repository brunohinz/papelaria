package br.pucpr.papelaria

import br.pucpr.papelaria.exceptions.RequisicaoInvalidaException
import br.pucpr.papelaria.produto.SortDir
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class SortDirTest {
    @Test
    fun `find aceita desc sem diferenciar maiusculas`() {
        assertEquals(SortDir.DESC, SortDir.find("DeSc"))
    }

    @Test
    fun `find aceita asc`() {
        assertEquals(SortDir.ASC, SortDir.find("asc"))
    }

    @Test
    fun `find rejeita valores invalidos`() {
        assertThrows<RequisicaoInvalidaException> { SortDir.find("qualquer") }
    }
}
