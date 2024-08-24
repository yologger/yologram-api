package link.yologram.api.rest.resource.bms

import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.bms.BoardService
import link.yologram.api.domain.bms.dto.*
import link.yologram.api.rest.support.wrapCreated
import link.yologram.api.rest.support.wrapOk
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bms/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class BmsResource(
    private val boardService: BoardService
) {

    @PostMapping("/board", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createBoard(@Validated @RequestBody request: CreateBoardRequest) = boardService.createBoard(uid = request.uid, title = request.title, body = request.body).wrapCreated()

    @PatchMapping("/board")
    fun editBoard(@Validated @RequestBody request: EditBoardRequest) = boardService.editBoard(uid = request.uid, bid = request.bid, newTitle = request.title, newBody = request.body).wrapOk()

    @DeleteMapping("/board", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteBoard(@Validated @RequestBody request: DeleteBoardRequest) = boardService.deleteBoard(uid = request.uid, bid = request.bid).wrapOk()

    @GetMapping("/board/{bid}")
    fun getBoard(@PathVariable(name = "bid") bid: Long) = boardService.getBoard(bid).wrapOk()

    @GetMapping("/user/{uid}/boards", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getBoardsByUid(
        @PathVariable(name = "uid") uid: Long,
        @Validated @RequestBody request: GetBoardsByUidRequest
    ) = boardService.getBoardsByUid(uid = uid, page = request.page, size = request.size).wrapOk()

    @GetMapping("/boards", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getBoards(@Validated @RequestBody request: GetBoardsRequest)
    = boardService.getBoards(page = request.page, size = request.size)
}