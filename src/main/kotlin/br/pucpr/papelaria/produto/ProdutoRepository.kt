package br.pucpr.papelaria.produto

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface ProdutoRepository : JpaRepository<Produto, Long> {
    @Query(
        """
            select p from Produto p
            where (:categoria is null or p.categoria.id = :categoria)
            and (:precoMin is null or p.preco >= :precoMin)
            and (:precoMax is null or p.preco <= :precoMax)
            and (:nome is null or lower(p.nome) like lower(concat('%', cast(:nome as string), '%')))
        """
    )
    fun filtrar(categoria: Long?, precoMin: BigDecimal?, precoMax: BigDecimal?, nome: String?, sort: Sort): List<Produto>

    fun existsByCategoriaId(categoriaId: Long): Boolean
}
