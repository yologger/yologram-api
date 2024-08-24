package link.yologram.api.rest.advice


import link.yologram.api.rest.response.GlobalErrorResponse
import link.yologram.api.rest.support.Response
import link.yologram.api.rest.support.wrapInternalServerError
import link.yologram.api.rest.support.wrapMethodNotAllowed
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
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