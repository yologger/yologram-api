package link.yologram.api.rest.advice

import link.yologram.api.domain.ums.dto.UmsErrorResponse
import link.yologram.api.domain.ums.exception.DuplicateUserException
import link.yologram.api.domain.ums.exception.UserNotFoundException
import link.yologram.api.rest.resource.ums.AuthResource
import link.yologram.api.rest.resource.ums.UserResource
import link.yologram.api.rest.support.Response
import link.yologram.api.rest.support.wrapConflict
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [UserResource::class, AuthResource::class])
@Order(Ordered.HIGHEST_PRECEDENCE)
class UmsExceptionHandler {

    @ExceptionHandler(value = [DuplicateUserException::class])
    fun handle(e: DuplicateUserException): ResponseEntity<Response<UmsErrorResponse>> {
        return UmsErrorResponse(message = e.message).wrapConflict()
    }

    @ExceptionHandler(value = [UserNotFoundException::class])
    fun handle(e: UserNotFoundException): ResponseEntity<Response<UmsErrorResponse>> {
        return UmsErrorResponse(message = e.message).wrapConflict()
    }
}