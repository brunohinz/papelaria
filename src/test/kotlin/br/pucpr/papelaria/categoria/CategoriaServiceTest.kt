package br.pucpr.papelaria.categoria

import br.pucpr.papelaria.exceptions.ConflitoException
import br.pucpr.papelaria.exceptions.NaoEncontradoException
import br.pucpr.papelaria.produto.ProdutoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional
import kotlin.test.assertEquals

class CategoriaServiceTest {
    private val repository = mockk<CategoriaRepository>(relaxUnitFun = true)
    private val produtoRepository = mockk<ProdutoRepository>()
    private val service = CategoriaService(repository, produtoRepository)

    @Test
    fun `insert salva categoria com nome novo`() {
        every { repository.existsByNomeIgnoreCase("Cadernos") } returns false
        every { repository.save(any()) } answers { firstArg<Categoria>().apply { id = 1L } }

        val salva = service.insert(Categoria(nome = "Cadernos"))

        assertEquals(1L, salva.id)
        assertEquals("Cadernos", salva.nome)
    }

    @Test
    fun `insert recusa nome duplicado`() {
        every { repository.existsByNomeIgnoreCase("Cadernos") } returns true

        assertThrows<ConflitoException> { service.insert(Categoria(nome = "Cadernos")) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `update altera o nome`() {
        val categoria = Categoria(id = 1, nome = "Velho")
        every { repository.findById(1L) } returns Optional.of(categoria)
        every { repository.existsByNomeIgnoreCaseAndIdNot("Novo", 1L) } returns false
        every { repository.save(any()) } answers { firstArg() }

        val salva = service.update(1L, "  Novo  ")

        assertEquals("Novo", salva.nome)
    }

    @Test
    fun `update recusa nome usado por outra categoria`() {
        every { repository.findById(1L) } returns Optional.of(Categoria(id = 1, nome = "Cadernos"))
        every { repository.existsByNomeIgnoreCaseAndIdNot("Canetas", 1L) } returns true

        assertThrows<ConflitoException> { service.update(1L, "Canetas") }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `delete remove quando nao ha produtos`() {
        val categoria = Categoria(id = 3, nome = "Vazia")
        every { repository.findById(3L) } returns Optional.of(categoria)
        every { produtoRepository.existsByCategoriaId(3L) } returns false

        service.delete(3L)

        verify { repository.delete(categoria) }
    }

    @Test
    fun `delete recusa categoria com produtos`() {
        every { repository.findById(3L) } returns Optional.of(Categoria(id = 3, nome = "Canetas"))
        every { produtoRepository.existsByCategoriaId(3L) } returns true

        assertThrows<ConflitoException> { service.delete(3L) }
        verify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    fun `findById lanca quando a categoria nao existe`() {
        every { repository.findById(9L) } returns Optional.empty()

        assertThrows<NaoEncontradoException> { service.findById(9L) }
    }
}
