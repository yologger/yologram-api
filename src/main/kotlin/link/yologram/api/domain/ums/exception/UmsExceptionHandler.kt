package link.yologram.api.domain.ums.exception

import link.yologram.api.domain.ums.dto.UmsErrorResponse
import link.yologram.api.domain.ums.resource.UserResource
import link.yologram.api.global.Response
import link.yologram.api.global.wrapBadRequest
import link.yologram.api.global.wrapConflict
import link.yologram.api.global.wrapUnauthorized
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [UserResource::class])
class UmsExceptionHandler {

    private val logger = LoggerFactory.getLogger(UmsExceptionHandler::class.java)

    @ExceptionHandler(value = [DuplicateUserException::class])
    fun handle(e: DuplicateUserException): ResponseEntity<Response<UmsErrorResponse>> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message, errorCode = UmsErrorCode.DUPLICATE_USER).wrapConflict()
    }

    @ExceptionHandler(value = [UserNotFoundException::class])
    fun handle(e: UserNotFoundException): ResponseEntity<Response<UmsErrorResponse>> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message, errorCode = UmsErrorCode.USER_NOT_FOUND).wrapBadRequest()
    }

    @ExceptionHandler(value = [UserAlreadyDeletedException::class])
    fun handle(e: UserAlreadyDeletedException): ResponseEntity<Response<UmsErrorResponse>> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message, errorCode = UmsErrorCode.USER_ALREADY_DELETED).wrapBadRequest()
    }

    @ExceptionHandler(value = [UmsException::class])
    fun handle(e: UmsException): ResponseEntity<Response<UmsErrorResponse>> {
        logger.error(e.message)
        return UmsErrorResponse(errorMessage = e.message!!, errorCode = UmsErrorCode.UNKNOWN_ERROR).wrapUnauthorized()
    }
}