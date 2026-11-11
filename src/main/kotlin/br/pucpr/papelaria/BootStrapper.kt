package br.pucpr.papelaria

import br.pucpr.papelaria.auth.Usuario
import br.pucpr.papelaria.auth.UsuarioRepository
import br.pucpr.papelaria.categoria.Categoria
import br.pucpr.papelaria.categoria.CategoriaRepository
import br.pucpr.papelaria.produto.Produto
import br.pucpr.papelaria.produto.ProdutoRepository
import jakarta.transaction.Transactional
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class BootStrapper(
    val usuarioRepository: UsuarioRepository,
    val categoriaRepository: CategoriaRepository,
    val produtoRepository: ProdutoRepository,
    val passwordEncoder: PasswordEncoder
) : ApplicationListener<ContextRefreshedEvent> {

    @Transactional
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (usuarioRepository.findByUsuario("admin") == null) {
            usuarioRepository.save(Usuario(usuario = "admin", senha = passwordEncoder.encode("admin123")!!))
        }
        if (categoriaRepository.count() == 0L) {
            val cadernos = categoriaRepository.save(Categoria(nome = "Cadernos"))
            val canetas = categoriaRepository.save(Categoria(nome = "Canetas"))
            produtoRepository.save(Produto(nome = "Caderno 10 materias", preco = BigDecimal("29.90"), categoria = cadernos))
            produtoRepository.save(Produto(nome = "Caneta azul", preco = BigDecimal("2.50"), categoria = canetas))
            produtoRepository.save(Produto(nome = "Caneta preta", preco = BigDecimal("2.50"), categoria = canetas))
        }
    }
}
