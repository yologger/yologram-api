package link.yologram.api.rest.advice

import link.yologram.api.domain.bms.dto.BmsErrorResponse
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.WrongBoardWriterException
import link.yologram.api.domain.ums.exception.UserNotFoundException
import link.yologram.api.rest.resource.bms.BmsResource
import link.yologram.api.rest.support.Response
import link.yologram.api.rest.support.wrapBadRequest
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [BmsResource::class])
@Order(Ordered.HIGHEST_PRECEDENCE)
class BmsExceptionHandler {

    private val logger = LoggerFactory.getLogger(BmsExceptionHandler::class.java)

    @ExceptionHandler(value = [UserNotFoundException::class])
    fun handle(e: UserNotFoundException): ResponseEntity<Response<BmsErrorResponse>> {
        logger.error(e.message)
        return BmsErrorResponse(message = e.message!!).wrapBadRequest()
    }

    @ExceptionHandler(value = [BoardNotFoundException::class])
    fun handle(e: BoardNotFoundException): ResponseEntity<Response<BmsErrorResponse>> {
        logger.error(e.message)
        return BmsErrorResponse(message = e.message!!).wrapBadRequest()
    }

    @ExceptionHandler(value = [WrongBoardWriterException::class])
    fun handle(e: WrongBoardWriterException): ResponseEntity<Response<BmsErrorResponse>> {
        logger.error(e.message)
        return BmsErrorResponse(message = e.message!!).wrapBadRequest()
    }
}