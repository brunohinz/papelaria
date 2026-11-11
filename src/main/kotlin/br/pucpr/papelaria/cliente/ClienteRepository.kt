package br.pucpr.papelaria.cliente

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClienteRepository : JpaRepository<Cliente, Long> {
    fun existsByEmailIgnoreCase(email: String): Boolean
    fun existsByEmailIgnoreCaseAndIdNot(email: String, id: Long): Boolean
}
