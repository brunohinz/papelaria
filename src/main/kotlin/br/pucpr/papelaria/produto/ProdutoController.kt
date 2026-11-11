package br.pucpr.papelaria.produto

import br.pucpr.papelaria.produto.requests.ProdutoRequest
import br.pucpr.papelaria.produto.responses.ProdutoResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/produtos")
class ProdutoController(
    val service: ProdutoService
) {
    @GetMapping
    fun list(
        @RequestParam(required = false) @Positive categoria: Long?,
        @RequestParam(required = false) @PositiveOrZero precoMin: BigDecimal?,
        @RequestParam(required = false) @PositiveOrZero precoMax: BigDecimal?,
        @RequestParam(required = false) @Size(max = 100) nome: String?,
        @RequestParam(defaultValue = "id") ordenar: String,
        @RequestParam(defaultValue = "asc") direcao: String
    ) = service.findAll(categoria, precoMin, precoMax, nome, ordenar, SortDir.find(direcao))
        .map { ProdutoResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = ResponseEntity.ok(ProdutoResponse(service.findById(id)))

    @PostMapping
    fun insert(@Valid @RequestBody produto: ProdutoRequest) =
        service.insert(produto.toProduto(), produto.categoriaId!!)
            .let { ProdutoResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody produto: ProdutoRequest) =
        ResponseEntity.ok(ProdutoResponse(service.update(id, produto.toProduto(), produto.categoriaId!!)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
