package br.pucpr.papelaria.categoria

import br.pucpr.papelaria.categoria.requests.CategoriaRequest
import br.pucpr.papelaria.categoria.responses.CategoriaResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categorias")
class CategoriaController(
    val service: CategoriaService
) {
    @GetMapping
    fun list() = service.findAll()
        .map { CategoriaResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = ResponseEntity.ok(CategoriaResponse(service.findById(id)))

    @PostMapping
    fun insert(@Valid @RequestBody categoria: CategoriaRequest) = service.insert(categoria.toCategoria())
        .let { CategoriaResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody categoria: CategoriaRequest) =
        ResponseEntity.ok(CategoriaResponse(service.update(id, categoria.nome!!)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
