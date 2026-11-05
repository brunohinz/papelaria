package br.pucpr.papelaria.categoria

import br.pucpr.papelaria.exceptions.NaoEncontradoException
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CategoriaService(
    val repository: CategoriaRepository
) {
    fun findAll(): List<Categoria> = repository.findAll()

    fun findById(id: Long): Categoria =
        repository.findByIdOrNull(id) ?: throw NaoEncontradoException("Categoria $id nao encontrada")

    fun insert(categoria: Categoria): Categoria =
        repository.save(categoria).also { log.info("Categoria criada: {}", it.id) }

    fun update(id: Long, nome: String): Categoria {
        val categoria = findById(id)
        categoria.nome = nome
        log.info("Categoria atualizada: {}", id)
        return repository.save(categoria)
    }

    fun delete(id: Long) {
        val categoria = findById(id)
        if (categoria.produtos.isNotEmpty()) throw IllegalStateException("Categoria possui produtos")
        repository.delete(categoria)
        log.info("Categoria removida: {}", id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(CategoriaService::class.java)
    }
}
