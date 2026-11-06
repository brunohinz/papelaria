package br.pucpr.papelaria.pedido

import br.pucpr.papelaria.cliente.ClienteService
import br.pucpr.papelaria.exceptions.NaoEncontradoException
import br.pucpr.papelaria.exceptions.PedidoInvalidoException
import br.pucpr.papelaria.produto.ProdutoService
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PedidoService(
    val repository: PedidoRepository,
    val clienteService: ClienteService,
    val produtoService: ProdutoService
) {
    fun findAll(): List<Pedido> = repository.findAll()

    fun findById(id: Long): Pedido =
        repository.findByIdOrNull(id) ?: throw NaoEncontradoException("Pedido $id nao encontrado")

    fun insert(clienteId: Long): Pedido {
        val cliente = clienteService.findByIdOrNull(clienteId)
            ?: throw PedidoInvalidoException("Cliente $clienteId nao existe")
        val pedido = repository.save(Pedido(cliente = cliente))
        log.info("Pedido {} criado para cliente {}", pedido.id, cliente.id)
        return pedido
    }

    @Transactional
    fun addItem(id: Long, produtoId: Long, quantidade: Int): Pedido {
        val pedido = findById(id)
        val produto = produtoService.findById(produtoId)
        if (pedido.itens.any { it.produto?.id == produto.id })
            throw PedidoInvalidoException("Produto ${produto.id} ja esta no pedido")
        pedido.itens.add(ItemPedido(pedido = pedido, produto = produto, quantidade = quantidade, precoUnitario = produto.preco))
        log.info("Produto {} adicionado ao pedido {}", produto.id, id)
        return repository.save(pedido)
    }

    @Transactional
    fun removeItem(id: Long, produtoId: Long): Pedido {
        val pedido = findById(id)
        if (!pedido.itens.removeIf { it.produto?.id == produtoId })
            throw PedidoInvalidoException("Produto $produtoId nao esta no pedido")
        log.info("Produto {} removido do pedido {}", produtoId, id)
        return repository.save(pedido)
    }

    companion object {
        private val log = LoggerFactory.getLogger(PedidoService::class.java)
    }
}
