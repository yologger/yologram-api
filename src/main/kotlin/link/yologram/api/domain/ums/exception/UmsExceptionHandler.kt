package link.yologram.api.domain.ums.exception

import link.yologram.api.domain.ums.model.UmsErrorResponse
import link.yologram.api.global.*
import link.yologram.api.global.rest.wrapBadRequest
import link.yologram.api.global.rest.wrapConflict
import link.yologram.api.global.rest.wrapNotFound
import link.yologram.api.global.rest.wrapUnauthorized
import link.yologram.api.global.rest.wrapUnprocessableEntity
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 2)
class UmsExceptionHandler {

    private val logger = LoggerFactory.getLogger(UmsExceptionHandler::class.java)

    @ExceptionHandler(value = [UserDuplicateException::class])
    fun handle(e: UserDuplicateException): ResponseEntity<UmsErrorResponse> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message, errorCode = UmsErrorCode.USER_DUPLICATE).wrapConflict()
    }

    @ExceptionHandler(value = [UserNotFoundException::class])
    fun handle(e: UserNotFoundException): ResponseEntity<UmsErrorResponse> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message, errorCode = UmsErrorCode.USER_NOT_FOUND).wrapNotFound()
    }

    @ExceptionHandler(value = [AuthWrongPasswordException::class])
    fun handle(e: AuthWrongPasswordException): ResponseEntity<UmsErrorResponse> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message!!, errorCode = UmsErrorCode.AUTH_WRONG_PASSWORD).wrapUnauthorized()
    }

    @ExceptionHandler(value = [AuthTokenCreationFailureException::class])
    fun handle(e: AuthTokenCreationFailureException): ResponseEntity<UmsErrorResponse> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message!!, errorCode = UmsErrorCode.AUTH_TOKEN_CREATION_FAILURE).wrapUnauthorized()
    }

    @ExceptionHandler(value = [AuthTokenExpiredException::class])
    fun handle(e: AuthTokenExpiredException): ResponseEntity<UmsErrorResponse> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message!!, errorCode = UmsErrorCode.AUTH_EXPIRED_TOKEN).wrapUnauthorized()
    }

    @ExceptionHandler(value = [AuthTokenInvalidException::class])
    fun handle(e: AuthTokenInvalidException): ResponseEntity<UmsErrorResponse> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message!!, errorCode = UmsErrorCode.AUTH_INVALID_TOKEN).wrapUnauthorized()
    }

    @ExceptionHandler(value = [AuthInvalidTokenOwnerException::class])
    fun handle(e: AuthInvalidTokenOwnerException): ResponseEntity<UmsErrorResponse> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message!!, errorCode = UmsErrorCode.AUTH_INVALID_TOKEN_OWNER).wrapUnauthorized()
    }

    @ExceptionHandler(value = [UserAlreadyDeletedException::class])
    fun handle(e: UserAlreadyDeletedException): ResponseEntity<UmsErrorResponse> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message, errorCode = UmsErrorCode.USER_ALREADY_DELETED).wrapUnprocessableEntity()
    }

    @ExceptionHandler(value = [AuthHeaderEmptyException::class])
    fun handle(e: AuthHeaderEmptyException): ResponseEntity<UmsErrorResponse> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message!!, errorCode = UmsErrorCode.AUTH_HEADER_EMPTY).wrapUnauthorized()
    }

    @ExceptionHandler(value = [UmsException::class])
    fun handle(e: UmsException): ResponseEntity<UmsErrorResponse> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message!!, errorCode = UmsErrorCode.UMS_UNKNOWN_ERROR).wrapUnauthorized()
    }
}