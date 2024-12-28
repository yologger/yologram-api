package link.yologram.api.rest.advice

import link.yologram.api.domain.ums.dto.AuthErrorResponse
import link.yologram.api.domain.ums.dto.UmsErrorResponse
import link.yologram.api.domain.auth.exception.AuthException
import link.yologram.api.domain.ums.exception.DuplicateUserException
import link.yologram.api.domain.auth.exception.UserNotFoundException
import link.yologram.api.domain.auth.exception.WrongPasswordException
import link.yologram.api.domain.auth.resource.AuthResource
import link.yologram.api.rest.resource.ums.UserResource
import link.yologram.api.rest.support.Response
import link.yologram.api.rest.support.wrapBadRequest
import link.yologram.api.rest.support.wrapConflict
import link.yologram.api.rest.support.wrapUnauthorized
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [UserResource::class, AuthResource::class])
@Order(Ordered.HIGHEST_PRECEDENCE)
class UmsExceptionHandler {

    private val logger = LoggerFactory.getLogger(UmsExceptionHandler::class.java)

    @ExceptionHandler(value = [DuplicateUserException::class])
    fun handle(e: DuplicateUserException): ResponseEntity<Response<UmsErrorResponse>> {
        logger.error(e.message)
        return UmsErrorResponse(message = e.message).wrapConflict()
    }

    @ExceptionHandler(value = [UserNotFoundException::class])
    fun handle(e: UserNotFoundException): ResponseEntity<Response<UmsErrorResponse>> {
        logger.error(e.message)
        return UmsErrorResponse(message = e.message).wrapBadRequest()
    }

    @ExceptionHandler(value = [WrongPasswordException::class])
    fun handle(e: WrongPasswordException): ResponseEntity<Response<AuthErrorResponse>> {
        logger.error(e.message)
        return AuthErrorResponse(message = e.message).wrapUnauthorized()
    }

    @ExceptionHandler(value = [AuthException::class])
    fun handle(e: AuthException): ResponseEntity<Response<AuthErrorResponse>> {
        logger.error(e.message)
        return AuthErrorResponse(message = e.message!!).wrapUnauthorized()
    }
}