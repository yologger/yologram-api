package link.yologram.api.domain.search.exception

import link.yologram.api.domain.search.model.SearchErrorResponse
import link.yologram.api.global.rest.wrapNotFound
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 2)
class SearchExceptionHandler {

    private val logger = LoggerFactory.getLogger(SearchExceptionHandler::class.java)

    @ExceptionHandler(value = [UserNotFoundException::class])
    fun handle(e: UserNotFoundException): ResponseEntity<SearchErrorResponse> {
        logger.error(e.message)
        return SearchErrorResponse(errorMessage = e.message!!, errorCode = SearchErrorCode.USER_NOT_FOUND).wrapNotFound()
    }
}