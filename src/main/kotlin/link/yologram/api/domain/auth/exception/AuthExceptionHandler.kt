package link.yologram.api.domain.auth.exception

import link.yologram.api.domain.auth.exception.AuthException
import link.yologram.api.domain.auth.dto.AuthErrorResponse
import link.yologram.api.global.Response
import link.yologram.api.global.wrapUnauthorized
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

    @ExceptionHandler(value = [UserNotFoundException::class])
    fun handle(e: UserNotFoundException): ResponseEntity<Response<AuthErrorResponse>> {
        logger.error(e.message)
        return AuthErrorResponse(errorMessage = e.message!!, errorCode = AuthErrorCode.USER_NOT_FOUND).wrapUnauthorized()
    }

    @ExceptionHandler(value = [WrongPasswordException::class])
    fun handle(e: WrongPasswordException): ResponseEntity<Response<AuthErrorResponse>> {
        logger.error(e.message)
        return AuthErrorResponse(errorMessage = e.message!!, errorCode = AuthErrorCode.WRONG_PASSWORD).wrapUnauthorized()
    }

    @ExceptionHandler(value = [InvalidTokenOwnerException::class])
    fun handle(e: InvalidTokenOwnerException): ResponseEntity<Response<AuthErrorResponse>> {
        logger.error(e.message)
        return AuthErrorResponse(errorMessage = e.message!!, errorCode = AuthErrorCode.INVALID_TOKEN_OWNER).wrapUnauthorized()
    }

    @ExceptionHandler(value = [ExpiredTokenException::class])
    fun handle(e: ExpiredTokenException): ResponseEntity<Response<AuthErrorResponse>> {
        logger.error(e.message)
        return AuthErrorResponse(errorMessage = e.message!!, errorCode = AuthErrorCode.EXPIRED_TOKEN).wrapUnauthorized()
    }

    @ExceptionHandler(value = [InvalidTokenException::class])
    fun handle(e: InvalidTokenException): ResponseEntity<Response<AuthErrorResponse>> {
        logger.error(e.message)
        return AuthErrorResponse(errorMessage = e.message!!, errorCode = AuthErrorCode.INVALID_TOKEN).wrapUnauthorized()
    }

    @ExceptionHandler(value = [TokenCreationFailException::class])
    fun handle(e: TokenCreationFailException): ResponseEntity<Response<AuthErrorResponse>> {
        logger.error(e.message)
        return AuthErrorResponse(errorMessage = e.message!!, errorCode = AuthErrorCode.TOKEN_CREATION_FAIL).wrapUnauthorized()
    }

    @ExceptionHandler(value = [AuthException::class])
    fun handle(e: AuthException): ResponseEntity<Response<AuthErrorResponse>> {
        logger.error(e.message)
        return AuthErrorResponse(errorMessage = e.message!!, errorCode = AuthErrorCode.UNKNOWN_ERROR).wrapUnauthorized()
    }
}