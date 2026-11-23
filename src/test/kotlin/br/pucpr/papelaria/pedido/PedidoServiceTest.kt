package br.pucpr.papelaria.pedido

import br.pucpr.papelaria.cliente.Cliente
import br.pucpr.papelaria.cliente.ClienteService
import br.pucpr.papelaria.exceptions.NaoEncontradoException
import br.pucpr.papelaria.exceptions.PedidoInvalidoException
import br.pucpr.papelaria.produto.Produto
import br.pucpr.papelaria.produto.ProdutoService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PedidoServiceTest {
    private val repository = mockk<PedidoRepository>(relaxUnitFun = true)
    private val clienteService = mockk<ClienteService>()
    private val produtoService = mockk<ProdutoService>()
    private val service = PedidoService(repository, clienteService, produtoService)

    private fun cliente() = Cliente(id = 7, nome = "Ana", email = "ana@teste.com")

    @Test
    fun `insert recusa cliente inexistente`() {
        every { clienteService.findByIdOrNull(7L) } returns null

        assertThrows<PedidoInvalidoException> { service.insert(7L) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `insert cria pedido para o cliente`() {
        val cliente = cliente()
        every { clienteService.findByIdOrNull(7L) } returns cliente
        every { repository.save(any()) } answers { firstArg<Pedido>().apply { id = 10L } }

        val pedido = service.insert(7L)

        assertEquals(10L, pedido.id)
        assertEquals(cliente, pedido.cliente)
        assertTrue(pedido.itens.isEmpty())
    }

    @Test
    fun `addItem congela o preco do produto`() {
        val produto = Produto(id = 5, nome = "Caneta azul", preco = BigDecimal("2.50"))
        val pedido = Pedido(id = 10, cliente = cliente())
        every { repository.findById(10L) } returns Optional.of(pedido)
        every { produtoService.findById(5L) } returns produto
        every { repository.save(any()) } answers { firstArg() }

        val salvo = service.addItem(10L, 5L, 3)
        val item = salvo.itens.single()

        assertEquals(produto, item.produto)
        assertEquals(3, item.quantidade)
        assertEquals(BigDecimal("2.50"), item.precoUnitario)
        assertEquals(BigDecimal("7.50"), salvo.total)

        // o preco do catalogo muda depois; o item fica com o valor da inclusao
        produto.preco = BigDecimal("9.99")
        assertEquals(BigDecimal("2.50"), item.precoUnitario)
        assertEquals(BigDecimal("7.50"), salvo.total)
    }

    @Test
    fun `addItem recusa produto repetido`() {
        val produto = Produto(id = 5, nome = "Caneta azul", preco = BigDecimal("2.50"))
        val pedido = Pedido(id = 10, cliente = cliente())
        pedido.itens.add(ItemPedido(pedido = pedido, produto = produto, quantidade = 1, precoUnitario = BigDecimal("2.50")))
        every { repository.findById(10L) } returns Optional.of(pedido)
        every { produtoService.findById(5L) } returns produto

        assertThrows<PedidoInvalidoException> { service.addItem(10L, 5L, 2) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `removeItem tira o produto do pedido`() {
        val produto = Produto(id = 5, nome = "Lapis", preco = BigDecimal("1.50"))
        val pedido = Pedido(id = 3, cliente = cliente())
        pedido.itens.add(ItemPedido(pedido = pedido, produto = produto, quantidade = 2, precoUnitario = BigDecimal("1.50")))
        every { repository.findById(3L) } returns Optional.of(pedido)
        every { repository.save(any()) } answers { firstArg() }

        val atualizado = service.removeItem(3L, 5L)

        assertTrue(atualizado.itens.isEmpty())
        assertEquals(BigDecimal.ZERO, atualizado.total)
    }

    @Test
    fun `removeItem recusa produto que nao esta no pedido`() {
        val pedido = Pedido(id = 3, cliente = cliente())
        every { repository.findById(3L) } returns Optional.of(pedido)

        assertThrows<PedidoInvalidoException> { service.removeItem(3L, 9L) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `delete remove o pedido`() {
        val pedido = Pedido(id = 3, cliente = cliente())
        every { repository.findById(3L) } returns Optional.of(pedido)

        service.delete(3L)

        verify { repository.delete(pedido) }
    }

    @Test
    fun `findById lanca quando o pedido nao existe`() {
        every { repository.findById(9L) } returns Optional.empty()

        assertThrows<NaoEncontradoException> { service.findById(9L) }
    }
}
