package link.yologram.api.domain.bms.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.bms.service.BoardService
import link.yologram.api.domain.bms.model.*
import link.yologram.api.domain.bms.model.board.BoardDataWithMetrics
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.model.APIEnvelopCursorPage
import link.yologram.api.global.model.APIEnvelopPage
import link.yologram.api.global.rest.wrapCreated
import link.yologram.api.global.rest.wrapOk
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "게시글", description = "게시글 관련 엔드포인트 (bms/BoardResource)")
@RestController
@RequestMapping("/api/bms/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class BoardResource(
    private val boardService: BoardService
) {

    private val logger = LoggerFactory.getLogger(BoardResource::class.java)

    @Operation(
        summary = "게시글 작성",
        description = "uid, title, body로 게시글을 생성한다.",
    )
    @PostMapping("/board", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createBoard(@Validated @RequestBody request: CreateBoardRequest) =
        boardService.createBoard(uid = request.uid, title = request.title, content = request.content).wrapCreated()

    @Operation(
        summary = "게시글 조회",
        description = "bid로 게시글을 생성한다.",
    )
    @GetMapping("/board/{bid}")
    fun getBoard(@PathVariable(name = "bid") @Validated @Min(1) bid: Long): ResponseEntity<APIEnvelop<BoardDataWithMetrics?>> {
        return boardService.getBoard(bid).wrapOk()
    }

    @Operation(
        summary = "게시글 수정",
        description = "uid, bid로 게시글을 생성한다.",
    )
    @PatchMapping("/board")
    fun editBoard(@Validated @RequestBody request: EditBoardRequest) =
        boardService.editBoard(uid = request.uid, bid = request.bid, newTitle = request.title, newBody = request.body)
            .wrapOk()

    @Operation(
        summary = "게시글 삭제",
        description = "uid, bid로 게시글을 생성한다.",
    )
    @DeleteMapping("/board", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteBoard(@Validated @RequestBody request: DeleteBoardRequest) =
        boardService.deleteBoard(uid = request.uid, bid = request.bid).wrapOk()

    @Operation(
        summary = "최신 게시글 조회",
        description = "Home 화면의 Infinite Scrolling 용. cursor based pagination",
    )
    @GetMapping("/boards", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getBoards(
        @Validated @RequestBody request: GetBoardsRequest
    ): ResponseEntity<APIEnvelopCursorPage<BoardDataWithMetrics>> {
        return boardService.getBoardsWithMetrics(nextCursorId = request.nextCursor, size = request.size).wrapOk()
    }


    @GetMapping("/user/{uid}/boards", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getBoardsByUid(
        @PathVariable(name = "uid") uid: Long,
        @RequestParam page: Long,
        @RequestParam size: Long
    ): ResponseEntity<APIEnvelopPage<BoardDataWithMetrics>> {
        return boardService.getBoardsByUid(uid = uid, page = page, size = size).wrapOk()
    }
}