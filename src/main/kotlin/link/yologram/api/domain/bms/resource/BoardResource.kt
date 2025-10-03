package link.yologram.api.domain.bms.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Max
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.bms.service.BoardService
import link.yologram.api.domain.bms.model.*
import link.yologram.api.domain.bms.model.board.BoardData
import link.yologram.api.domain.bms.model.board.BoardDataWithMetrics
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.model.APIEnvelopCursorPage
import link.yologram.api.global.model.APIEnvelopPage
import link.yologram.api.global.rest.docs.ApiParameterAuthToken
import link.yologram.api.global.rest.docs.ApiResponseInvalidArgument
import link.yologram.api.global.rest.docs.ApiResponseUnauthorized
import link.yologram.api.global.rest.wrapCreated
import link.yologram.api.global.rest.wrapOk
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "게시글", description = "게시글 관련 엔드포인트 (bms/BoardResource)")
@RestController
@ApiResponseInvalidArgument
@RequestMapping("/api/bms/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class BoardResource(
    private val boardService: BoardService
) {

    private val logger = LoggerFactory.getLogger(BoardResource::class.java)

    @Operation(summary = "게시글 작성", description = "title, content로 게시글을 생성한다.")
    @ApiParameterAuthToken
    @ApiResponseUnauthorized
    @ApiResponse(
        responseCode = "201",
        description = "게시글 생성 성공",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = BoardData::class),
            )
        ]
    )
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 유저",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = BmsErrorResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                            "errorMessage": "User not exist",
                            "errorCode": "USER_NOT_FOUND"
                        }"""
                    )
                ]
            )
        ]
    )
    @PostMapping("/board", consumes = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
    fun createBoard(
        @Validated @RequestBody request: CreateBoardRequest,
        @Parameter(hidden = true) authData: AuthData
    ) =
        boardService.createBoard(uid = authData.uid, title = request.title, content = request.content).wrapCreated()

    @Operation(summary = "게시글 조회", description = "bid로 게시글을 생성한다.",)
    @ApiResponse(
        responseCode = "200",
        description = "게시글 조회 성공",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = BoardDataWithMetrics::class)
            )
        ]
    )
    @GetMapping("/board/{bid}")
    fun getBoard(@PathVariable(name = "bid") @Validated @Min(1) bid: Long): ResponseEntity<APIEnvelop<BoardDataWithMetrics?>> {
        return boardService.getBoard(bid).wrapOk()
    }

    @Operation(summary = "게시글 수정", description = "bid로 게시글을 수정한다.")
    @ApiParameterAuthToken
    @ApiResponseUnauthorized
    @ApiResponse(
        responseCode = "200",
        description = "게시글 수정 성공",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = BoardData::class),
            )
        ]
    )
    @ApiResponse(
        responseCode = "403",
        description = "uid가 board의 작성자가 아닐 때",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = BmsErrorResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                            "errorMessage": "Wrong board writer",
                            "errorCode": "BOARD_WRONG_WRITER"
                        }"""
                    )
                ]
            )
        ]
    )
    @ApiResponse(
        responseCode = "404",
        description = "user가 존재하지 않거나, board가 존재하지 않음",
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
    @PatchMapping("/board/{bid}")
    fun editBoard(
        @PathVariable(name = "bid") @Validated @Min(1) bid: Long,
        @Validated @RequestBody request: EditBoardRequest,
        @Parameter(hidden = true) authData: AuthData
    ) = boardService.editBoard(uid = authData.uid, bid = bid, newTitle = request.title, newBody = request.body) .wrapOk()

    @Operation(summary = "게시글 삭제", description = "bid로 게시글을 삭제한다.")
    @ApiParameterAuthToken
    @ApiResponseUnauthorized
    @ApiResponse(
        responseCode = "200",
        description = "게시글 삭제 성공",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = DeleteBoardResponse::class),
            )
        ]
    )
    @ApiResponse(
        responseCode = "403",
        description = "uid가 board의 작성자가 아닐 때",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = BmsErrorResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                            "errorMessage": "Wrong board writer",
                            "errorCode": "BOARD_WRONG_WRITER"
                        }"""
                    )
                ]
            )
        ]
    )
    @ApiResponse(
        responseCode = "404",
        description = "user가 존재하지 않거나, board가 존재하지 않음",
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
    @DeleteMapping("/board/{bid}", consumes = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
    fun deleteBoard(
        @PathVariable(name = "bid") @Validated @Min(1) bid: Long,
        @Parameter(hidden = true) authData: AuthData
    ) = boardService.deleteBoard(uid = authData.uid, bid = bid).wrapOk()

    @Operation(
        summary = "최신 게시글 조회",
        description = "Home 화면의 Infinite Scrolling 용. cursor based pagination",
    )
    @GetMapping("/boards")
    fun getBoards(
        @RequestParam(defaultValue = "10") @Min(1) @Max(value = 40) size: Long = 10,
        @RequestParam(required = false) nextCursor: String? = null

    ): ResponseEntity<APIEnvelopCursorPage<BoardDataWithMetrics>> {
        return boardService.getBoardsWithMetrics(size = size, nextCursorId = nextCursor).wrapOk()
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