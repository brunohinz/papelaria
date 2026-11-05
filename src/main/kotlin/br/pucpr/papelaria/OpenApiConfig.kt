package br.pucpr.papelaria

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Backend Papelaria")
                .description("API de papelaria (categorias, produtos, clientes e pedidos)")
                .version("0.0.1")
        )
        .components(
            Components().addSecuritySchemes(
                "bearer-jwt",
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Cole o JWT retornado por POST /auth/login (sem o prefixo Bearer)")
            )
        )
        .addSecurityItem(SecurityRequirement().addList("bearer-jwt"))
}
