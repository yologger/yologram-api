package link.yologram.api.domain.bms.resource

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.bms.model.*
import link.yologram.api.domain.bms.service.BoardViewService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "게시글/조회수", description = "게시글 조회수 엔드포인트 (bms/BoardViewResource)")
@RestController
@RequestMapping("/api/bms/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class BoardViewResource(
    private val boardViewService: BoardViewService
) {

    @PostMapping("/board/{bid}/view", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun recordBoardView(
        @PathVariable(name = "bid") bid: Long,
        servletRequest: HttpServletRequest,
        @Validated @RequestBody(required = false) request: RecordBoardViewRequest?,
    ): ResponseEntity<Void> {
        val uid = request?.uid
        val ip = getClientIp(servletRequest)
        boardViewService.recordView(boardId = bid, uid = uid, ip = ip)
        return ResponseEntity.noContent().build<Void>()
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val header = request.getHeader("X-Forwarded-For")
        return if (header != null && header.isNotEmpty()) {
            header.split(",")[0].trim() // X-Forwarded-For 값이 여러 개인 경우, 첫 번째가 실제 클라이언트 IP
        } else {
            request.remoteAddr
        }
    }
}