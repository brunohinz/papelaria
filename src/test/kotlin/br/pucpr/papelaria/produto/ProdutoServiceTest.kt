package br.pucpr.papelaria.produto

import br.pucpr.papelaria.categoria.Categoria
import br.pucpr.papelaria.categoria.CategoriaService
import br.pucpr.papelaria.exceptions.ConflitoException
import br.pucpr.papelaria.exceptions.ProdutoNaoEncontradoException
import br.pucpr.papelaria.exceptions.RequisicaoInvalidaException
import br.pucpr.papelaria.pedido.ItemPedidoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProdutoServiceTest {
    private val repository = mockk<ProdutoRepository>(relaxUnitFun = true)
    private val categoriaService = mockk<CategoriaService>()
    private val itemPedidoRepository = mockk<ItemPedidoRepository>()
    private val service = ProdutoService(repository, categoriaService, itemPedidoRepository)

    @Test
    fun `findAll rejeita campo de ordenacao invalido`() {
        assertThrows<RequisicaoInvalidaException> {
            service.findAll(null, null, null, null, "senha", SortDir.ASC)
        }
        verify(exactly = 0) { repository.filtrar(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `findAll rejeita precoMin maior que precoMax`() {
        assertThrows<RequisicaoInvalidaException> {
            service.findAll(null, BigDecimal("10"), BigDecimal("5"), null, "id", SortDir.ASC)
        }
        verify(exactly = 0) { repository.filtrar(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `findAll filtra nome sem espacos e ordena desc`() {
        every { repository.filtrar(any(), any(), any(), any(), any()) } returns emptyList()

        service.findAll(2L, BigDecimal("1.00"), BigDecimal("10.00"), "  caneta  ", "preco", SortDir.DESC)

        verify {
            repository.filtrar(
                2L,
                BigDecimal("1.00"),
                BigDecimal("10.00"),
                "caneta",
                match { it.getOrderFor("preco")?.direction == Sort.Direction.DESC }
            )
        }
    }

    @Test
    fun `findAll trata nome em branco como sem filtro`() {
        every { repository.filtrar(any(), any(), any(), any(), any()) } returns emptyList()

        service.findAll(null, null, null, "   ", "id", SortDir.ASC)

        verify {
            repository.filtrar(
                null,
                null,
                null,
                null,
                match { it.getOrderFor("id")?.isAscending == true }
            )
        }
    }

    @Test
    fun `findById lanca quando o produto nao existe`() {
        every { repository.findById(9L) } returns Optional.empty()

        assertThrows<ProdutoNaoEncontradoException> { service.findById(9L) }
    }

    @Test
    fun `insert associa a categoria e salva`() {
        val categoria = Categoria(id = 1, nome = "Canetas")
        val produto = Produto(nome = "Caneta azul", preco = BigDecimal("2.50"))
        every { categoriaService.findById(1L) } returns categoria
        every { repository.save(any()) } answers { firstArg<Produto>().apply { id = 8L } }

        val salvo = service.insert(produto, 1L)

        assertEquals(8L, salvo.id)
        assertEquals(categoria, salvo.categoria)
        assertNull(salvo.descricao)
    }

    @Test
    fun `update altera dados e categoria`() {
        val atual = Produto(id = 4, nome = "Velho", preco = BigDecimal("1.00"), categoria = Categoria(id = 1, nome = "A"))
        val nova = Categoria(id = 2, nome = "B")
        every { repository.findById(4L) } returns Optional.of(atual)
        every { categoriaService.findById(2L) } returns nova
        every { repository.save(any()) } answers { firstArg() }

        val salvo = service.update(4L, Produto(nome = "Novo", descricao = "ponta fina", preco = BigDecimal("3.00")), 2L)

        assertEquals("Novo", salvo.nome)
        assertEquals("ponta fina", salvo.descricao)
        assertEquals(BigDecimal("3.00"), salvo.preco)
        assertEquals(nova, salvo.categoria)
    }

    @Test
    fun `delete remove produto que nao esta em pedido`() {
        val produto = Produto(id = 4, nome = "Lapis", preco = BigDecimal("1.00"))
        every { repository.findById(4L) } returns Optional.of(produto)
        every { itemPedidoRepository.existsByProdutoId(4L) } returns false

        service.delete(4L)

        verify { repository.delete(produto) }
    }

    @Test
    fun `delete recusa produto que esta em pedido`() {
        every { repository.findById(4L) } returns Optional.of(Produto(id = 4, nome = "Lapis", preco = BigDecimal("1.00")))
        every { itemPedidoRepository.existsByProdutoId(4L) } returns true

        assertThrows<ConflitoException> { service.delete(4L) }
        verify(exactly = 0) { repository.delete(any()) }
    }
}
