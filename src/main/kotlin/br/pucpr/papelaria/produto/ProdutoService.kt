package br.pucpr.papelaria.produto

import br.pucpr.papelaria.categoria.CategoriaService
import br.pucpr.papelaria.exceptions.ProdutoNaoEncontradoException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ProdutoService(
    val repository: ProdutoRepository,
    val categoriaService: CategoriaService
) {
    fun findAll(categoria: Long?, precoMax: BigDecimal?, ordenar: String, dir: SortDir): List<Produto> {
        val campos = listOf("id", "nome", "preco")
        if (ordenar !in campos) throw IllegalArgumentException("ordenar deve ser um de $campos")
        val sort = if (dir == SortDir.DESC) Sort.by(ordenar).descending() else Sort.by(ordenar)
        return repository.filtrar(categoria, precoMax, sort)
    }

    fun findById(id: Long): Produto = repository.findByIdOrNull(id) ?: throw ProdutoNaoEncontradoException(id)

    fun insert(produto: Produto, categoriaId: Long): Produto {
        produto.categoria = categoriaService.findById(categoriaId)
        return repository.save(produto).also { log.info("Produto criado: {} - {}", it.id, it.nome) }
    }

    fun update(id: Long, dados: Produto, categoriaId: Long): Produto {
        val produto = findById(id)
        produto.nome = dados.nome
        produto.descricao = dados.descricao
        produto.preco = dados.preco
        produto.categoria = categoriaService.findById(categoriaId)
        log.info("Produto atualizado: {}", id)
        return repository.save(produto)
    }

    fun delete(id: Long) {
        repository.delete(findById(id))
        log.info("Produto removido: {}", id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProdutoService::class.java)
    }
}
