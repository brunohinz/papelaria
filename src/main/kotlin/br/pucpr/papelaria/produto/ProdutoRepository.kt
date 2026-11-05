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
            and (:precoMax is null or p.preco <= :precoMax)
        """
    )
    fun filtrar(categoria: Long?, precoMax: BigDecimal?, sort: Sort): List<Produto>
}
