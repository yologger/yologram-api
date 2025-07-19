package link.yologram.api.domain.bms.resource

import any
import link.yologram.api.common.WebMvcTestSupport
import link.yologram.api.config.JwtConfig
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.BoardWrongWriterException
import link.yologram.api.domain.bms.exception.InvalidPaginationCursorException
import link.yologram.api.domain.bms.exception.UserNotFoundException
import link.yologram.api.domain.bms.model.DeleteBoardRequest
import link.yologram.api.domain.bms.model.DeleteBoardResponse
import link.yologram.api.domain.bms.model.EditBoardRequest
import link.yologram.api.domain.bms.model.GetBoardsRequest
import link.yologram.api.domain.bms.model.board.BoardData
import link.yologram.api.domain.bms.model.board.BoardDataWithMetrics
import link.yologram.api.domain.bms.service.BoardService
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.model.APIEnvelopCursorPage
import link.yologram.api.global.model.APIEnvelopPage
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import kotlin.test.Test
import link.yologram.api.domain.bms.model.CreateBoardRequest as CreateBoardRequest1


@WebMvcTestSupport(controllers = [BoardResource::class])
class BoardResourceTest(
    @Autowired val client: WebTestClient,
    @Autowired val jwtConfig: JwtConfig
) {

    @MockBean
    lateinit var boardService: BoardService

    @Nested
    @DisplayName("게시글 등록")
    inner class CreateBoardTest {

        @Test
        @DisplayName("게시글 등록에 성공하면 201을 응답한다")
        fun `게시글 등록에 성공하면 201을 응답한다`() {
            val request = CreateBoardRequest1(uid = 1, title = "title", content = "content")
            val response =
                APIEnvelop(
                    BoardData(
                        1L,
                        1L,
                        "title",
                        "body",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                    )
                )

            given(
                boardService.createBoard(any(), any(), any())
            ).willReturn(response)

            client.post()
                .uri("/api/bms/v1/board")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated
        }

        @Test
        @DisplayName("User가 존재하지 않으면 400 에러가 발생한다")
        fun `User가 존재하지 않으면 400 에러가 발생한다`() {
            val request = CreateBoardRequest1(uid = 1, title = "title from uid=1", content = "content from uid1=")

            given(
                boardService.createBoard(any(), any(), any())
            ).willThrow(UserNotFoundException("User not exist"))

            client.post()
                .uri("/api/bms/v1/board")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo("User not exist")
                .jsonPath("$.errorCode").isEqualTo("USER_NOT_FOUND")
        }
    }

    @Nested
    @DisplayName("게시글 조회")
    inner class GetBoardTest {

        @Test
        @DisplayName("게시글 ID로 조회 시 200과 게시글을 응답한다")
        fun `게시글 ID로 조회 시 200과 게시글을 응답한다`() {
            val bid = 123L
            val board = BoardDataWithMetrics(
                bid,
                "title",
                "body",
                LocalDateTime.now(),
                LocalDateTime.now(),
                writer = BoardDataWithMetrics.Writer(
                    uid = 1L,
                    name = "tester",
                    nickname = "tester",
                    avatar = null
                ),
                metrics = BoardDataWithMetrics.Metrics(
                    commentCount = 5,
                    likeCount = 3,
                    viewCount = 1
                )
            )

            given(
                boardService.getBoard(bid)
            ).willReturn(APIEnvelop(data = board))

            client.get()
                .uri("/api/bms/v1/board/$bid")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data.bid").isEqualTo(bid)
        }

        @Test
        @DisplayName("게시글 ID 조회 시 존재하지 않으면 'data' 필드가 empty다")
        fun `게시글 ID 조회 시 존재하지 않으면 'data' 필드가 empty다`() {
            val bid = 123L
            val board = null

            given(
                boardService.getBoard(bid)
            ).willReturn(APIEnvelop(data = board))

            client.get()
                .uri("/api/bms/v1/board/$bid")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data").isEmpty
        }
    }

    @Nested
    @DisplayName("게시글 수정")
    inner class EditBoardTest {

        @Test
        @DisplayName("게시글 수정 시 200을 응답한다")
        fun `게시글 수정 시 200을 응답한다`() {
            val request = EditBoardRequest(uid = 1, bid = 1L, title = "new title", body = "new body")

            given(
                boardService.editBoard(any(), any(), any(), any())
            ).willReturn(
                APIEnvelop(
                    BoardData(
                        1L,
                        1L,
                        "new title",
                        "new body",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                    )
                )
            )

            client.patch()
                .uri("/api/bms/v1/board")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
        }

        @Test
        @DisplayName("게시글 수정 시 작성자가 아닌 경우 400을 응답한다")
        fun `게시글 수정 시 작성자가 아닌 경우 403을 응답한다`() {
            val request = EditBoardRequest(uid = 2, bid = 1L, title = "hack", body = "hackBody")
            given(
                boardService.editBoard(
                    any(),
                    any(),
                    any(),
                    any()
                )
            ).willThrow(BoardWrongWriterException("Wrong board writer"))

            client.patch()
                .uri("/api/bms/v1/board")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo("Wrong board writer")
                .jsonPath("$.errorCode").isEqualTo("BOARD_WRONG_WRITER")
        }

        @Test
        @DisplayName("게시글 수정 시 게시글이 존재하지 않으면 404를 응답한다")
        fun `게시글 수정 시 게시글이 존재하지 않으면 404를 응답한다`() {
            val request = EditBoardRequest(uid = 1, bid = 999L, title = "notfound", body = "notfound")
            given(
                boardService.editBoard(
                    any(),
                    any(),
                    any(),
                    any()
                )
            ).willThrow(BoardNotFoundException("Board not found"))

            client.patch()
                .uri("/api/bms/v1/board")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo("Board not found")
                .jsonPath("$.errorCode").isEqualTo("BOARD_NOT_FOUND")
        }
    }

    @Nested
    @DisplayName("게시글 삭제")
    inner class DeleteBoardTest {
        @Test
        @DisplayName("게시글 삭제 시 200을 응답한다")
        fun `게시글 삭제 시 200을 응답한다`() {

            val request = DeleteBoardRequest(uid = 1, bid = 1L)
            val response = APIEnvelop(DeleteBoardResponse(1, 1L))
            given(boardService.deleteBoard(any(), any())).willReturn(response)

            client.method(HttpMethod.DELETE)
                .uri("/api/bms/v1/board")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
        }

        @Test
        @DisplayName("게시글 삭제 시 게시글이 존재하지 않으면 400로 응답한다")
        fun `게시글 삭제 시 게시글이 존재하지 않으면 400로 응답한다`() {
            val request = DeleteBoardRequest(uid = 1, bid = 999L)
            given(boardService.deleteBoard(any(), any())).willThrow(BoardNotFoundException("Board not found"))

            client.method(HttpMethod.DELETE)
                .uri("/api/bms/v1/board")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo("Board not found")
                .jsonPath("$.errorCode").isEqualTo("BOARD_NOT_FOUND")
        }

        @Test
        @DisplayName("게시글 삭제 시 작성자가 아닌 경우 403을 응답한다")
        fun `게시글 삭제 시 작성자가 아닌 경우 403을 응답한다`() {
            val request = DeleteBoardRequest(uid = 2, bid = 1L)
            given(boardService.deleteBoard(any(), any())).willThrow(BoardWrongWriterException("Wrong board writer"))

            client.method(HttpMethod.DELETE)
                .uri("/api/bms/v1/board")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo("Wrong board writer")
                .jsonPath("$.errorCode").isEqualTo("BOARD_WRONG_WRITER")
        }
    }

    @Nested
    @DisplayName("최신 게시글 조회")
    inner class GetBoardsTest {

        @Test
        @DisplayName("cursor 기반 게시글 목록 조회")
        fun `cursor 기반 게시글 목록 조회`() {
            val request = GetBoardsRequest(nextCursor = null, size = 10)
            val response = APIEnvelopCursorPage<BoardDataWithMetrics>(nextCursor = null, data = emptyList())
            given(boardService.getBoardsWithMetrics(any(), any())).willReturn(response)

            client.method(HttpMethod.GET)
                .uri("/api/bms/v1/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
        }


        @Test
        @DisplayName("cursor 기반 게시글 목록 조회 실패 시 400을 응답한다")
        fun `cursor 기반 게시글 목록 조회 실패 시 400을 응답한다`() {
            val request = GetBoardsRequest(nextCursor = "invalid-cursor", size = 10)
            given(
                boardService.getBoardsWithMetrics(any(), any())
            ).willThrow(InvalidPaginationCursorException("Invalid cursor"))

            client.method(HttpMethod.GET)
                .uri("/api/bms/v1/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo("Invalid cursor")
                .jsonPath("$.errorCode").isEqualTo("INVALID_PAGINATION_CURSOR")
        }
    }

    @Nested
    @DisplayName("유저의 게시글 목록 조회")
    inner class GetBoardsByUidTest {

        @Test
        @DisplayName("게시글 목록 조회가 성공하면 200을 반환한다")
        fun `게시글 목록 조회가 성공하면 200을 반환한다`() {

            val uid: Long = 1
            val page: Long = 0
            val size: Long = 2

            val boards = listOf(
                BoardDataWithMetrics(
                    1L,
                    "title",
                    "body",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    writer = BoardDataWithMetrics.Writer(
                        uid = 1L,
                        name = "tester",
                        nickname = "tester",
                        avatar = null
                    ),
                    metrics = BoardDataWithMetrics.Metrics(
                        commentCount = 5,
                        likeCount = 3,
                        viewCount = 1
                    )
                ),
                BoardDataWithMetrics(
                    2L,
                    "title",
                    "body",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    writer = BoardDataWithMetrics.Writer(
                        uid = 1L,
                        name = "tester",
                        nickname = "tester",
                        avatar = null
                    ),
                    metrics = BoardDataWithMetrics.Metrics(
                        commentCount = 5,
                        likeCount = 3,
                        viewCount = 1
                    )
                )
            )

            val response = APIEnvelopPage(
                data = boards,
                page = page,
                size = size,
                totalPages = 1,
                totalCount = 2,
                first = true,
                last = true
            )

            given(boardService.getBoardsByUid(any(), any(), any())).willReturn(response)

            client.method(HttpMethod.GET)
                .uri("/api/bms/v1/user/$uid/boards?page=$page&size=$size")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data.length()").isEqualTo(2)
        }

        @Test
        @DisplayName("게시글이 없을 경우 200과 빈 리스트를 반환한다")
        fun `게시글이 없을 경우 200과 빈 리스트를 반환한다`() {

            val uid: Long = 1
            val page: Long = 0
            val size: Long = 2

            val boards = listOf<BoardDataWithMetrics>()

            val response = APIEnvelopPage(
                data = boards,
                page = page,
                size = size,
                totalPages = 1,
                totalCount = 0,
                first = true,
                last = true
            )

            given(boardService.getBoardsByUid(any(), any(), any())).willReturn(response)

            client.method(HttpMethod.GET)
                .uri("/api/bms/v1/user/$uid/boards?page=$page&size=$size")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data.length()").isEqualTo(0)
        }
    }
}
