package link.yologram.api.domain.bms.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.bms.model.comment.CommentData
import link.yologram.api.domain.bms.model.comment.CreateCommentRequest
import link.yologram.api.domain.bms.service.CommentService
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.model.APIEnvelopPage
import link.yologram.api.global.rest.wrapCreated
import link.yologram.api.global.rest.wrapOk
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "게시글/댓글", description = "게시글/댓글 엔드포인트 (bms/BoardCommentResource)")
@RestController
@RequestMapping("/api/bms/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class BoardCommentResource(
    private val commentService: CommentService
) {

    @Operation(
        summary = "댓글 작성",
        description = "bid, uid, content로 댓글을 작성한다."
    )
    @PostMapping("/board/{bid}/comment")
    fun createComment(
        authData: AuthData,
        @PathVariable bid: Long,
        @Validated @RequestBody request: CreateCommentRequest
    ): ResponseEntity<APIEnvelop<CommentData>> {
        return commentService.createComment(boardId = bid, userId = authData.uid, content = request.content).wrapCreated()
    }

    @DeleteMapping("/board/{bid}/comment/{cid}")
    fun deleteComment(
        authData: AuthData,
        @PathVariable bid: Long,
        @PathVariable cid: Long,
    ): ResponseEntity<Void>{
        commentService.deleteComment(boardId = bid, commentId = cid)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/board/{bid}/comments")
    fun getCommentsByBid(
        @PathVariable bid: Long
    ): ResponseEntity<APIEnvelopPage<CommentData>> {
        return commentService.getCommentsByBid(bid = bid).wrapOk()
    }
}