package link.yologram.api.domain.auth.advice

import link.yologram.api.domain.auth.exception.AuthException
import link.yologram.api.domain.ums.dto.AuthErrorResponse
import link.yologram.api.rest.support.Response
import link.yologram.api.rest.support.wrapUnauthorized
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 2)
class AuthExceptionHandler {
    private val logger = LoggerFactory.getLogger(AuthExceptionHandler::class.java)

    @ExceptionHandler(value = [AuthException::class])
    fun handle(e: AuthException): ResponseEntity<Response<AuthErrorResponse>> {
        logger.error(e.message)
        return AuthErrorResponse(message = e.message!!).wrapUnauthorized()
    }
}