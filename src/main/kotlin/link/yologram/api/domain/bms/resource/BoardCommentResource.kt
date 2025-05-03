package link.yologram.api.domain.bms.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.bms.dto.comment.CommentData
import link.yologram.api.domain.bms.dto.comment.CreateCommentRequest
import link.yologram.api.domain.bms.service.CommentService
import link.yologram.api.global.Response
import link.yologram.api.global.wrapCreated
import link.yologram.api.global.wrapOk
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
        @PathVariable bid: Long,
        @Validated @RequestBody request: CreateCommentRequest
    ): ResponseEntity<Response<CommentData>> {
        return commentService.createComment(bid = bid, uid = request.uid, content = request.content).wrapCreated()
    }

    @GetMapping("/board/{bid}/comments")
    fun getCommentsByBid(
        @PathVariable bid: Long
    ): ResponseEntity<Response<List<CommentData>>> {
        return commentService.getCommentsByBid(bid = bid).wrapOk()
    }
}