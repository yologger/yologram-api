package link.yologram.api.rest.advice

import link.yologram.api.rest.response.GlobalErrorResponse
import link.yologram.api.rest.support.Response
import link.yologram.api.rest.support.wrapBadRequest
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE+1)
class ValidationExceptionHandler {

    private val logger = LoggerFactory.getLogger(ValidationExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(e: MethodArgumentNotValidException): ResponseEntity<Response<GlobalErrorResponse>> {
        logger.error(e.message)
        val fieldErrors = e.bindingResult.fieldErrors
        val fieldError = fieldErrors[fieldErrors.size - 1]
        val errorMessage  = fieldError.defaultMessage ?: "${fieldError.field} field has invalid value"
        return GlobalErrorResponse(message = errorMessage).wrapBadRequest()
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(e: HttpMessageNotReadableException): ResponseEntity<Response<GlobalErrorResponse>> {
        logger.error(e.message)
        return GlobalErrorResponse(message = "Json parse error").wrapBadRequest()
    }

}