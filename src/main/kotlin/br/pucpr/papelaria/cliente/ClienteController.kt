package br.pucpr.papelaria.cliente

import br.pucpr.papelaria.cliente.requests.ClienteRequest
import br.pucpr.papelaria.cliente.responses.ClienteResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clientes")
class ClienteController(
    val service: ClienteService
) {
    @GetMapping
    fun list() = service.findAll()
        .map { ClienteResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = ResponseEntity.ok(ClienteResponse(service.findById(id)))

    @PostMapping
    fun insert(@Valid @RequestBody cliente: ClienteRequest) = service.insert(cliente.toCliente())
        .let { ClienteResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody cliente: ClienteRequest) =
        ResponseEntity.ok(ClienteResponse(service.update(id, cliente.toCliente())))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
