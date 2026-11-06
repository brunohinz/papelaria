package br.pucpr.papelaria.cliente

import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ClienteService(
    val repository: ClienteRepository
) {
    fun findAll(): List<Cliente> = repository.findAll()

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    fun insert(cliente: Cliente): Cliente =
        repository.save(cliente).also { log.info("Cliente criado: {}", it.id) }

    companion object {
        private val log = LoggerFactory.getLogger(ClienteService::class.java)
    }
}
