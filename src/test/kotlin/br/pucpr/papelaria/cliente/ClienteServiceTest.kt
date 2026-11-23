package br.pucpr.papelaria.cliente

import br.pucpr.papelaria.exceptions.ConflitoException
import br.pucpr.papelaria.exceptions.NaoEncontradoException
import br.pucpr.papelaria.pedido.PedidoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ClienteServiceTest {
    private val repository = mockk<ClienteRepository>(relaxUnitFun = true)
    private val pedidoRepository = mockk<PedidoRepository>()
    private val service = ClienteService(repository, pedidoRepository)

    @Test
    fun `insert salva cliente com email novo`() {
        every { repository.existsByEmailIgnoreCase("ana@teste.com") } returns false
        every { repository.save(any()) } answers { firstArg<Cliente>().apply { id = 1L } }

        val salvo = service.insert(Cliente(nome = "Ana", email = "ana@teste.com"))

        assertEquals(1L, salvo.id)
        assertEquals("ana@teste.com", salvo.email)
    }

    @Test
    fun `insert recusa email duplicado`() {
        every { repository.existsByEmailIgnoreCase("ana@teste.com") } returns true

        assertThrows<ConflitoException> { service.insert(Cliente(nome = "Ana", email = "ana@teste.com")) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `update troca nome e email`() {
        val cliente = Cliente(id = 1, nome = "Ana", email = "ana@teste.com")
        every { repository.findById(1L) } returns Optional.of(cliente)
        every { repository.existsByEmailIgnoreCaseAndIdNot("ana2@teste.com", 1L) } returns false
        every { repository.save(any()) } answers { firstArg() }

        val salvo = service.update(1L, Cliente(nome = "Ana Souza", email = "ana2@teste.com"))

        assertEquals("Ana Souza", salvo.nome)
        assertEquals("ana2@teste.com", salvo.email)
    }

    @Test
    fun `update recusa email de outro cliente`() {
        every { repository.findById(1L) } returns Optional.of(Cliente(id = 1, nome = "Ana", email = "ana@teste.com"))
        every { repository.existsByEmailIgnoreCaseAndIdNot("outro@teste.com", 1L) } returns true

        assertThrows<ConflitoException> { service.update(1L, Cliente(nome = "Ana", email = "outro@teste.com")) }
        verify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `delete remove cliente sem pedidos`() {
        val cliente = Cliente(id = 2, nome = "Ana", email = "ana@teste.com")
        every { repository.findById(2L) } returns Optional.of(cliente)
        every { pedidoRepository.existsByClienteId(2L) } returns false

        service.delete(2L)

        verify { repository.delete(cliente) }
    }

    @Test
    fun `delete recusa cliente com pedidos`() {
        every { repository.findById(2L) } returns Optional.of(Cliente(id = 2, nome = "Ana", email = "ana@teste.com"))
        every { pedidoRepository.existsByClienteId(2L) } returns true

        assertThrows<ConflitoException> { service.delete(2L) }
        verify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    fun `findById lanca quando o cliente nao existe`() {
        every { repository.findById(9L) } returns Optional.empty()

        assertThrows<NaoEncontradoException> { service.findById(9L) }
        assertNull(service.findByIdOrNull(9L))
    }
}
