package br.pucpr.papelaria

import br.pucpr.papelaria.exceptions.NaoEncontradoException
import br.pucpr.papelaria.exceptions.PedidoInvalidoException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {
    private fun erro(status: HttpStatus, msg: String?) =
        ResponseEntity.status(status).body(mapOf("status" to status.value(), "erro" to msg))

    @ExceptionHandler(NaoEncontradoException::class)
    fun naoEncontrado(e: NaoEncontradoException) = erro(HttpStatus.NOT_FOUND, e.message).also { log.warn(e.message) }

    @ExceptionHandler(PedidoInvalidoException::class, IllegalArgumentException::class, IllegalStateException::class)
    fun invalido(e: RuntimeException) = erro(HttpStatus.BAD_REQUEST, e.message).also { log.warn(e.message) }

    @ExceptionHandler(ResponseStatusException::class)
    fun status(e: ResponseStatusException) = erro(HttpStatus.valueOf(e.statusCode.value()), e.reason)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validacao(e: MethodArgumentNotValidException) =
        erro(HttpStatus.BAD_REQUEST, e.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" })

    companion object {
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }
}
