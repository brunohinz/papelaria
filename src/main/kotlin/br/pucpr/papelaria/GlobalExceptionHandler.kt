package br.pucpr.papelaria

import br.pucpr.papelaria.exceptions.ConflitoException
import br.pucpr.papelaria.exceptions.NaoEncontradoException
import br.pucpr.papelaria.exceptions.RequisicaoInvalidaException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {
    private fun erro(status: HttpStatus, msg: String?, campos: Map<String, String>? = null): ResponseEntity<Map<String, Any?>> {
        val body = mutableMapOf<String, Any?>("status" to status.value(), "erro" to msg)
        if (!campos.isNullOrEmpty()) body["campos"] = campos
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(NaoEncontradoException::class, NoResourceFoundException::class)
    fun naoEncontrado(e: Exception) = erro(HttpStatus.NOT_FOUND, e.message).also { log.warn(e.message) }

    @ExceptionHandler(RequisicaoInvalidaException::class, IllegalArgumentException::class)
    fun invalido(e: RuntimeException) = erro(HttpStatus.BAD_REQUEST, e.message).also { log.warn(e.message) }

    @ExceptionHandler(ConflitoException::class)
    fun conflito(e: ConflitoException) = erro(HttpStatus.CONFLICT, e.message).also { log.warn(e.message) }

    // rede de seguranca: violacao de FK/unique que escapou das verificacoes dos services
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun integridade(e: DataIntegrityViolationException) =
        erro(HttpStatus.CONFLICT, "Operacao viola a integridade dos dados (registro em uso ou duplicado)")
            .also { log.warn("Violacao de integridade: {}", e.mostSpecificCause.message) }

    @ExceptionHandler(ResponseStatusException::class)
    fun status(e: ResponseStatusException) = erro(HttpStatus.valueOf(e.statusCode.value()), e.reason)

    // @Valid no corpo da requisicao
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validacao(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any?>> {
        val campos = e.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "invalido") }
        log.warn("Validacao falhou: {}", campos)
        return erro(HttpStatus.BAD_REQUEST, "Dados invalidos", campos)
    }

    // validacao de @RequestParam / @PathVariable
    @ExceptionHandler(HandlerMethodValidationException::class)
    fun validacaoParametros(e: HandlerMethodValidationException): ResponseEntity<Map<String, Any?>> {
        val campos = e.parameterValidationResults.associate { r ->
            (r.methodParameter.parameterName ?: "parametro") to r.resolvableErrors.joinToString("; ") { it.defaultMessage ?: "invalido" }
        }
        log.warn("Parametros invalidos: {}", campos)
        return erro(HttpStatus.BAD_REQUEST, "Parametros invalidos", campos)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun tipoInvalido(e: MethodArgumentTypeMismatchException) =
        erro(HttpStatus.BAD_REQUEST, "Valor invalido para '${e.name}': ${e.value}")

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun parametroFaltando(e: MissingServletRequestParameterException) =
        erro(HttpStatus.BAD_REQUEST, "Parametro obrigatorio ausente: ${e.parameterName}")

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun corpoInvalido(e: HttpMessageNotReadableException) =
        erro(HttpStatus.BAD_REQUEST, "Corpo da requisicao ausente ou JSON mal formado")

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun metodo(e: HttpRequestMethodNotSupportedException) = erro(HttpStatus.METHOD_NOT_ALLOWED, e.message)

    @ExceptionHandler(Exception::class)
    fun inesperado(e: Exception) =
        erro(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno").also { log.error("Erro inesperado", e) }

    companion object {
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }
}
