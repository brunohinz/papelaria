package br.pucpr.papelaria.security

import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Configuration
class SecurityConfig(val jwtFilter: JwtFilter) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(
        http: HttpSecurity,
        @Qualifier("handlerExceptionResolver") resolvers: ObjectProvider<HandlerExceptionResolver>
    ): SecurityFilterChain = http
        .csrf { it.disable() }
        .headers { h -> h.frameOptions { it.sameOrigin() } }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .authorizeHttpRequests {
            it.requestMatchers("/auth/**", "/h2-console/**", "/error", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            it.requestMatchers(HttpMethod.GET, "/**").permitAll()
            // DELETE so para ADMIN; POST e PUT exigem qualquer usuario logado
            it.requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
            it.anyRequest().authenticated()
        }
        .exceptionHandling {
            it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            // 403 cai no GlobalExceptionHandler (mesmo formato dos outros erros)
            it.accessDeniedHandler { request, response, ex ->
                resolvers.getObject().resolveException(request, response, null, ex)
            }
        }
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        .build()
}
