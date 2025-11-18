package link.yologram.api.domain.fraud.exception

import link.yologram.api.domain.bms.exception.BmsErrorCode
import link.yologram.api.domain.bms.exception.BoardCommentCountException
import link.yologram.api.domain.bms.exception.BoardCommentMismatchException
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.BoardWrongWriterException
import link.yologram.api.domain.bms.exception.CommentNotFoundException
import link.yologram.api.domain.bms.exception.InvalidPaginationCursorException
import link.yologram.api.domain.bms.exception.UserAlreadyLikeBoardException
import link.yologram.api.domain.bms.exception.UserNotFoundException
import link.yologram.api.domain.bms.exception.UserNotLikeBoardException
import link.yologram.api.domain.bms.model.BmsErrorResponse
import link.yologram.api.domain.fraud.model.FraudErrorResponse
import link.yologram.api.global.rest.wrapBadRequest
import link.yologram.api.global.rest.wrapForbidden
import link.yologram.api.global.rest.wrapInternalServerError
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
class FraudExceptionHandler {

    private val logger = LoggerFactory.getLogger(FraudExceptionHandler::class.java)

    @ExceptionHandler(value = [KisaWhoisException::class])
    fun handle(e: KisaWhoisException): ResponseEntity<FraudErrorResponse> {
        logger.error(e.message)
        return FraudErrorResponse(errorMessage = e.message!!, errorCode = FraudErrorCode.HTTP_REQUEST_ARGUMENT_INVALID).wrapBadRequest()
    }

    @ExceptionHandler(value = [NetworkException::class])
    fun handle(e: NetworkException): ResponseEntity<FraudErrorResponse> {
        logger.error(e.message)
        return FraudErrorResponse(errorMessage = e.message!!, errorCode = FraudErrorCode.NETWORK_ERROR).wrapInternalServerError()
    }

    @ExceptionHandler(value = [FraudException::class])
    fun handle(e: FraudException): ResponseEntity<FraudErrorResponse> {
        val exceptionTypeName = e::class.simpleName
        logger.error(exceptionTypeName)
        logger.error(e.message)
        return FraudErrorResponse(errorMessage = e.message!!, errorCode = FraudErrorCode.INTERNAL_ERROR).wrapInternalServerError()
    }
}