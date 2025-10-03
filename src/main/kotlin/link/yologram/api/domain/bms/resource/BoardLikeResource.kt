package link.yologram.api.domain.bms.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.bms.model.BmsErrorResponse
import link.yologram.api.domain.bms.model.LikeBoardResponse
import link.yologram.api.domain.bms.model.UnlikeBoardResponse
import link.yologram.api.domain.bms.model.board.BoardData
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.domain.bms.service.BoardLikeService
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.rest.docs.ApiParameterAuthTokenRequired
import link.yologram.api.global.rest.docs.ApiResponseInvalidArgument
import link.yologram.api.global.rest.docs.ApiResponseUnauthorized
import link.yologram.api.global.rest.wrapOk
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "게시글/좋아요", description = "게시글/좋아요 관련 엔드포인트 (bms/BoardLikeResource)")
@RestController
@ApiResponseInvalidArgument
@RequestMapping("/api/bms/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class BoardLikeResource(
    private val boardLikeService: BoardLikeService
) {

    @Operation(summary = "게시글 좋아요", description = "uid, bid로 게시글 좋아요")
    @ApiParameterAuthTokenRequired
    @ApiResponseUnauthorized
    @ApiResponse(
        responseCode = "200",
        description = "게시글 좋아요 성공",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = LikeBoardResponse::class),
            )
        ]
    )
    @ApiResponse(
        responseCode = "404",
        description = "게시글이 존재하지 않음",
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
    @ApiResponse(
        responseCode = "422",
        description = "이미 좋아요 상태",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = BmsErrorResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                            "errorMessage": "User (uid) already liked board (bid)",
                            "errorCode": "USER_ALREADY_LIKE_BOARD"
                        }"""
                    )
                ]
            )
        ]
    )
    @PostMapping("/board/like/{bid}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun likeBoard(
        @PathVariable(name = "bid") @Validated @Min(1) bid: Long,
        @Parameter(hidden = true) authData: AuthData
    ): ResponseEntity<APIEnvelop<LikeBoardResponse>> {
        return boardLikeService.likeBoard(uid = authData.uid, bid = bid).wrapOk()
    }

    @Operation(summary = "게시글 좋아요 취소", description = "uid, bid로 게시글 좋아요 취소")
    @ApiParameterAuthTokenRequired
    @ApiResponseUnauthorized
    @ApiResponse(
        responseCode = "404",
        description = "게시글이 존재하지 않음",
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
    @ApiResponse(
        responseCode = "422",
        description = "이전에 좋아요를 한 적이 없음",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = BmsErrorResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                            "errorMessage": "User (uid) not like board (bid)",
                            "errorCode": "USER_NOT_LIKE_BOARD"
                        }"""
                    )
                ]
            )
        ]
    )
    @DeleteMapping("/board/like/{bid}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun unlikeBoard(
        @PathVariable(name = "bid") @Validated @Min(1) bid: Long,
        @Parameter(hidden = true) authData: AuthData
    ): ResponseEntity<APIEnvelop<UnlikeBoardResponse>> {
        return boardLikeService.unlikeBoard(uid = authData.uid, bid = bid).wrapOk()
    }
}