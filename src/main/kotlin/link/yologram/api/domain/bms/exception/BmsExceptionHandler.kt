package link.yologram.api.domain.bms.exception

import link.yologram.api.domain.bms.model.BmsErrorResponse
import link.yologram.api.global.rest.wrapBadRequest
import link.yologram.api.global.rest.wrapForbidden
import link.yologram.api.global.rest.wrapNotFound
import link.yologram.api.global.rest.wrapUnauthorized
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 2)
class BmsExceptionHandler {

    private val logger = LoggerFactory.getLogger(BmsExceptionHandler::class.java)

    @ExceptionHandler(value = [UserNotFoundException::class])
    fun handle(e: UserNotFoundException): ResponseEntity<BmsErrorResponse> {
        logger.error(e.message)
        return BmsErrorResponse(errorMessage = e.message!!, errorCode = BmsErrorCode.USER_NOT_FOUND).wrapNotFound()
    }

    @ExceptionHandler(value = [BoardNotFoundException::class])
    fun handle(e: BoardNotFoundException): ResponseEntity<BmsErrorResponse> {
        logger.error(e.message)
        return BmsErrorResponse(errorMessage = e.message!!, errorCode = BmsErrorCode.BOARD_NOT_FOUND).wrapNotFound()
    }

    @ExceptionHandler(value = [BoardWrongWriterException::class])
    fun handle(e: BoardWrongWriterException): ResponseEntity<BmsErrorResponse> {
        logger.error(e.message)
        return BmsErrorResponse(errorMessage = e.message!!, errorCode = BmsErrorCode.BOARD_WRONG_WRITER).wrapForbidden()
    }

    @ExceptionHandler(value = [CommentNotFoundException::class])
    fun handle(e: CommentNotFoundException): ResponseEntity<BmsErrorResponse> {
        logger.error(e.message)
        return BmsErrorResponse(errorMessage = e.message!!, errorCode = BmsErrorCode.COMMENT_NOT_FOUND).wrapBadRequest()
    }

    @ExceptionHandler(value = [BoardCommentMismatchException::class])
    fun handle(e: BoardCommentMismatchException): ResponseEntity<BmsErrorResponse> {
        logger.error(e.message)
        return BmsErrorResponse(errorMessage = e.message!!, errorCode = BmsErrorCode.BOARD_COMMENT_MISMATCH).wrapBadRequest()
    }

    @ExceptionHandler(value = [BoardCommentCountException::class])
    fun handle(e: BoardCommentCountException): ResponseEntity<BmsErrorResponse> {
        logger.error(e.message)
        return BmsErrorResponse(errorMessage = e.message!!, errorCode = BmsErrorCode.BOARD_COMMENT_COUNT_NOT_FOUND).wrapBadRequest()
    }

    @ExceptionHandler(value = [UserAlreadyLikeBoardException::class])
    fun handle(e: UserAlreadyLikeBoardException): ResponseEntity<BmsErrorResponse> {
        logger.error(e.message)
        return BmsErrorResponse(errorMessage = e.message!!, errorCode = BmsErrorCode.USER_ALREADY_LIKE_BOARD).wrapBadRequest()
    }

    @ExceptionHandler(value = [UserNotLikeBoardException::class])
    fun handle(e: UserNotLikeBoardException): ResponseEntity<BmsErrorResponse> {
        logger.error(e.message)
        return BmsErrorResponse(errorMessage = e.message!!, errorCode = BmsErrorCode.USER_NOT_LIKE_BOARD).wrapBadRequest()
    }

    @ExceptionHandler(value = [InvalidPaginationCursorException::class])
    fun handle(e: InvalidPaginationCursorException): ResponseEntity<BmsErrorResponse> {
        logger.error(e.message)
        return BmsErrorResponse(errorMessage = e.message!!, errorCode = BmsErrorCode.INVALID_PAGINATION_CURSOR).wrapBadRequest()
    }
}