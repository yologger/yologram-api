package link.yologram.api.global.excpetion


import link.yologram.api.global.dto.GlobalErrorResponse
import link.yologram.api.global.Response
import link.yologram.api.global.wrapInternalServerError
import link.yologram.api.global.wrapMethodNotAllowed
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handle(e: HttpRequestMethodNotSupportedException): ResponseEntity<Response<GlobalErrorResponse>> {
        logger.error(e.message)
        return GlobalErrorResponse(message = "Http Request Method Not Allowed").wrapMethodNotAllowed()
    }

    @ExceptionHandler(Exception::class)
    fun handle(e: Exception): ResponseEntity<Response<GlobalErrorResponse>> {
        logger.error(e.message)
        return GlobalErrorResponse(message = "Internal Server Error").wrapInternalServerError()
    }
}