package br.pucpr.papelaria.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(val jwt: JWT) : OncePerRequestFilter() {
    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val header = req.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            jwt.extractUsuario(token)?.let { usuario ->
                val authorities = jwt.extractRole(token)
                    ?.let { listOf(SimpleGrantedAuthority("ROLE_$it")) }
                    ?: emptyList()
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(usuario, null, authorities)
            }
        }
        chain.doFilter(req, res)
    }
}
