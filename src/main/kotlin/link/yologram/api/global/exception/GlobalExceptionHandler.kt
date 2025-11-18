package link.yologram.api.global.exception


import link.yologram.api.global.rest.wrapInternalServerError
import link.yologram.api.global.rest.wrapMethodNotAllowed
import link.yologram.api.global.rest.wrapNotFound
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handle(e: HttpRequestMethodNotSupportedException): ResponseEntity<GlobalErrorResponse> {
        logger.error(e.message)
        return GlobalErrorResponse(errorMessage = "Http Request Method Not Allowed", errorCode = GlobalErrorCode.HTTP_REQUEST_METHOD_NOT_ALLOWED).wrapMethodNotAllowed()
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handle(e: NoHandlerFoundException): ResponseEntity<GlobalErrorResponse> {
        logger.error(e.message)
        return GlobalErrorResponse(errorMessage = e.localizedMessage, errorCode = GlobalErrorCode.NOT_FOUND).wrapNotFound()
    }

    @ExceptionHandler(Exception::class)
    fun handle(e: Exception): ResponseEntity<GlobalErrorResponse> {
        val exceptionTypeName = e::class.simpleName
        logger.error(exceptionTypeName)
        logger.error(e.message)
        return GlobalErrorResponse(errorMessage = "Internal Server Error", errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR).wrapInternalServerError()
    }
}