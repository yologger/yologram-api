package link.yologram.api.global.excpetion

import jakarta.validation.ConstraintViolationException
import link.yologram.api.global.rest.wrapBadRequest
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 1)
class ValidationExceptionHandler {

    private val logger = LoggerFactory.getLogger(ValidationExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(e: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        logger.error(e.message)
        val fieldErrors = e.bindingResult.fieldErrors
        val fieldError = fieldErrors[fieldErrors.size - 1]
        val errorMessage  = fieldError.defaultMessage ?: "${fieldError.field} field has invalid value"
        return ValidationErrorResponse(errorMessage = errorMessage, errorCode = ValidationErrorCode.HTTP_REQUEST_ARGUMENT_INVALID).wrapBadRequest()
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handle(e: MethodArgumentTypeMismatchException): ResponseEntity<ValidationErrorResponse> {
        logger.error(e.message)
        return ValidationErrorResponse(errorMessage = e.message, errorCode = ValidationErrorCode.HTTP_REQUEST_ARGUMENT_INVALID).wrapBadRequest()
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handle(e: HandlerMethodValidationException): ResponseEntity<ValidationErrorResponse> {
        logger.error(e.message)
        return ValidationErrorResponse(errorMessage = e.message, errorCode = ValidationErrorCode.HTTP_REQUEST_ARGUMENT_INVALID).wrapBadRequest()
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handle(e: ConstraintViolationException): ResponseEntity<ValidationErrorResponse> {
        logger.error(e.message)
        return ValidationErrorResponse(errorMessage = e.message, errorCode = ValidationErrorCode.HTTP_REQUEST_ARGUMENT_INVALID).wrapBadRequest()
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(e: HttpMessageNotReadableException): ResponseEntity<ValidationErrorResponse> {
        logger.error(e.message)
        return ValidationErrorResponse(errorMessage = "Json parse error", errorCode = ValidationErrorCode.HTTP_REQUEST_ARGUMENT_INVALID).wrapBadRequest()
    }

}