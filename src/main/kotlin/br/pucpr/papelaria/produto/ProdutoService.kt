package br.pucpr.papelaria.produto

import br.pucpr.papelaria.categoria.CategoriaService
import br.pucpr.papelaria.exceptions.ConflitoException
import br.pucpr.papelaria.exceptions.ProdutoNaoEncontradoException
import br.pucpr.papelaria.exceptions.RequisicaoInvalidaException
import br.pucpr.papelaria.pedido.ItemPedidoRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ProdutoService(
    val repository: ProdutoRepository,
    val categoriaService: CategoriaService,
    val itemPedidoRepository: ItemPedidoRepository
) {
    fun findAll(
        categoria: Long?, precoMin: BigDecimal?, precoMax: BigDecimal?, nome: String?,
        ordenar: String, dir: SortDir
    ): List<Produto> {
        if (ordenar !in CAMPOS_ORDENACAO) throw RequisicaoInvalidaException("ordenar deve ser um de $CAMPOS_ORDENACAO")
        if (precoMin != null && precoMax != null && precoMin > precoMax)
            throw RequisicaoInvalidaException("precoMin nao pode ser maior que precoMax")
        val sort = if (dir == SortDir.DESC) Sort.by(ordenar).descending() else Sort.by(ordenar)
        return repository.filtrar(categoria, precoMin, precoMax, nome?.trim()?.ifEmpty { null }, sort)
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
        val produto = findById(id)
        if (itemPedidoRepository.existsByProdutoId(id))
            throw ConflitoException("Produto $id esta em pedidos e nao pode ser removido")
        repository.delete(produto)
        log.info("Produto removido: {}", id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProdutoService::class.java)
        val CAMPOS_ORDENACAO = listOf("id", "nome", "preco")
    }
}
