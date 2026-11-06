package br.pucpr.papelaria.pedido

import br.pucpr.papelaria.pedido.requests.ItemRequest
import br.pucpr.papelaria.pedido.requests.PedidoRequest
import br.pucpr.papelaria.pedido.responses.PedidoResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pedidos")
class PedidoController(
    val service: PedidoService
) {
    @GetMapping
    fun list() = service.findAll()
        .map { PedidoResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = ResponseEntity.ok(PedidoResponse(service.findById(id)))

    @PostMapping
    fun insert(@Valid @RequestBody pedido: PedidoRequest) = service.insert(pedido.clienteId!!)
        .let { PedidoResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @PostMapping("/{id}/itens")
    fun addItem(@PathVariable id: Long, @Valid @RequestBody item: ItemRequest) =
        ResponseEntity.ok(PedidoResponse(service.addItem(id, item.produtoId!!, item.quantidade!!)))

    @DeleteMapping("/{id}/itens/{produtoId}")
    fun removeItem(@PathVariable id: Long, @PathVariable produtoId: Long) =
        ResponseEntity.ok(PedidoResponse(service.removeItem(id, produtoId)))
}
