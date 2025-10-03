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
import link.yologram.api.domain.bms.model.comment.CommentData
import link.yologram.api.domain.bms.model.comment.CreateCommentRequest
import link.yologram.api.domain.bms.service.BoardCommentService
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.model.APIEnvelopPage
import link.yologram.api.global.rest.docs.ApiParameterAuthToken
import link.yologram.api.global.rest.docs.ApiResponseInvalidArgument
import link.yologram.api.global.rest.docs.ApiResponseUnauthorized
import link.yologram.api.global.rest.wrapCreated
import link.yologram.api.global.rest.wrapOk
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "게시글/댓글", description = "게시글/댓글 엔드포인트 (bms/BoardCommentResource)")
@RestController
@ApiResponseInvalidArgument
@RequestMapping("/api/bms/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class BoardCommentResource(
    private val boardCommentService: BoardCommentService
) {

    @Operation(summary = "댓글 작성",  description = "bid, uid, content로 댓글을 작성한다.")
    @PostMapping("/board/{bid}/comment", consumes = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
    @ApiParameterAuthToken
    @ApiResponseUnauthorized
    @ApiResponse(
        responseCode = "201",
        description = "댓글 작성 완료",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = APIEnvelop::class),
                examples = [
                    ExampleObject(
                        value = """{
                            "data": {
                                "id": 6,
                                "bid": 3,
                                "uid": 32,
                                "content": "This is comment 1",
                                "createdDate": "2025-10-03T15:00:19.897565",
                                "modifiedDate": "2025-10-03T15:00:19.897565"
                            }
                        }"""
                    )
                ]
            )
        ]
    )
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 게시글",
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
    fun createComment(
        @PathVariable @Validated @Min(1) bid: Long,
        @Validated @RequestBody request: CreateCommentRequest,
        @Parameter(hidden = true) authData: AuthData
    ): ResponseEntity<APIEnvelop<CommentData>> {
        return boardCommentService.createComment(boardId = bid, userId = authData.uid, content = request.content).wrapCreated()
    }

    @Operation(summary = "댓글 삭제",  description = "bid, uid로 댓글을 작성한다.")
    @ApiParameterAuthToken
    @ApiResponseUnauthorized
    @ApiResponse(
        responseCode = "204",
        description = "댓글 삭제 완료",
    )
    @ApiResponse(
        responseCode = "404",
        description = "게시글이나 댓글이 존재하지 않음",
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
        description = "해당 게시글의 댓글이 아님",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = BmsErrorResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                            "errorMessage": "BoardComment does not belong to the board.",
                            "errorCode": "BOARD_COMMENT_MISMATCH"
                        }"""
                    )
                ]
            )
        ]
    )
    @DeleteMapping("/board/{bid}/comment/{cid}", consumes = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
    fun deleteComment(
        @PathVariable @Validated @Min(1) bid: Long,
        @PathVariable @Validated @Min(1) cid: Long,
        @Parameter(hidden = true) authData: AuthData
    ): ResponseEntity<Void>{
        boardCommentService.deleteComment(boardId = bid, commentId = cid)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/board/{bid}/comments")
    fun getCommentsByBid(
        @PathVariable bid: Long
    ): ResponseEntity<APIEnvelopPage<CommentData>> {
        return boardCommentService.getCommentsByBid(bid = bid).wrapOk()
    }
}