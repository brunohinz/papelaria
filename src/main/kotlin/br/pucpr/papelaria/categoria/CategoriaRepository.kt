package br.pucpr.papelaria.categoria

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoriaRepository : JpaRepository<Categoria, Long> {
    fun findByNome(nome: String): Categoria?
    fun existsByNomeIgnoreCase(nome: String): Boolean
    fun existsByNomeIgnoreCaseAndIdNot(nome: String, id: Long): Boolean
}
