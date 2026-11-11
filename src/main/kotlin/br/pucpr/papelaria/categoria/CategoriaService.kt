package br.pucpr.papelaria.categoria

import br.pucpr.papelaria.exceptions.ConflitoException
import br.pucpr.papelaria.exceptions.NaoEncontradoException
import br.pucpr.papelaria.produto.ProdutoRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CategoriaService(
    val repository: CategoriaRepository,
    val produtoRepository: ProdutoRepository
) {
    fun findAll(): List<Categoria> = repository.findAll()

    fun findById(id: Long): Categoria =
        repository.findByIdOrNull(id) ?: throw NaoEncontradoException("Categoria $id nao encontrada")

    fun insert(categoria: Categoria): Categoria {
        if (repository.existsByNomeIgnoreCase(categoria.nome))
            throw ConflitoException("Ja existe categoria com nome '${categoria.nome}'")
        return repository.save(categoria).also { log.info("Categoria criada: {}", it.id) }
    }

    fun update(id: Long, nome: String): Categoria {
        val categoria = findById(id)
        val novoNome = nome.trim()
        if (repository.existsByNomeIgnoreCaseAndIdNot(novoNome, id))
            throw ConflitoException("Ja existe categoria com nome '$novoNome'")
        categoria.nome = novoNome
        log.info("Categoria atualizada: {}", id)
        return repository.save(categoria)
    }

    fun delete(id: Long) {
        val categoria = findById(id)
        if (produtoRepository.existsByCategoriaId(id))
            throw ConflitoException("Categoria $id possui produtos e nao pode ser removida")
        repository.delete(categoria)
        log.info("Categoria removida: {}", id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(CategoriaService::class.java)
    }
}
