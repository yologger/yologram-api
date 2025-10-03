package link.yologram.api.domain.bms.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.Min
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.bms.model.*
import link.yologram.api.domain.bms.service.BoardViewService
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.global.rest.docs.ApiParameterAuthTokenOptional
import link.yologram.api.global.rest.docs.ApiResponseInvalidArgument
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "게시글/조회수", description = "게시글 조회수 엔드포인트 (bms/BoardViewResource)")
@RestController
@ApiResponseInvalidArgument
@RequestMapping("/api/bms/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class BoardViewResource(
    private val boardViewService: BoardViewService
) {

    @Operation(summary = "조회수 기록",  description = "(clientIp, userId)로 uid에 대한 조회수를 기록한다")
    @ApiParameterAuthTokenOptional
    @ApiResponse(
        responseCode = "204",
        description = "조회수 기록 성공"
    )
    @ApiResponse(
        responseCode = "404",
        description = "유저나 게시글이 존재하지 않음",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = BmsErrorResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                            "errorMessage": "Board not exist",
                            "errorCode": "BOARD_NOT_FOUND"
                        }"""
                    )
                ]
            )
        ]
    )
    @PostMapping("/board/{bid}/view")
    fun recordBoardView(
        @PathVariable @Validated @Min(1) bid: Long,
        servletRequest: HttpServletRequest,
        @Parameter(hidden = true) authData: AuthData?
    ): ResponseEntity<Void> {
        val uid = authData?.uid
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