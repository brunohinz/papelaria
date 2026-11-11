package br.pucpr.papelaria.cliente

import br.pucpr.papelaria.exceptions.ConflitoException
import br.pucpr.papelaria.exceptions.NaoEncontradoException
import br.pucpr.papelaria.pedido.PedidoRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ClienteService(
    val repository: ClienteRepository,
    val pedidoRepository: PedidoRepository
) {
    fun findAll(): List<Cliente> = repository.findAll()

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    fun findById(id: Long): Cliente =
        repository.findByIdOrNull(id) ?: throw NaoEncontradoException("Cliente $id nao encontrado")

    fun insert(cliente: Cliente): Cliente {
        if (repository.existsByEmailIgnoreCase(cliente.email))
            throw ConflitoException("Ja existe cliente com email ${cliente.email}")
        return repository.save(cliente).also { log.info("Cliente criado: {}", it.id) }
    }

    fun update(id: Long, dados: Cliente): Cliente {
        val cliente = findById(id)
        if (repository.existsByEmailIgnoreCaseAndIdNot(dados.email, id))
            throw ConflitoException("Ja existe cliente com email ${dados.email}")
        cliente.nome = dados.nome
        cliente.email = dados.email
        log.info("Cliente atualizado: {}", id)
        return repository.save(cliente)
    }

    fun delete(id: Long) {
        val cliente = findById(id)
        if (pedidoRepository.existsByClienteId(id))
            throw ConflitoException("Cliente $id possui pedidos e nao pode ser removido")
        repository.delete(cliente)
        log.info("Cliente removido: {}", id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ClienteService::class.java)
    }
}
