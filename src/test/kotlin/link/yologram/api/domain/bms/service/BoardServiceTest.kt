package link.yologram.api.domain.bms.service

import any
import link.yologram.api.domain.bms.entity.Board
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.BoardWrongWriterException
import link.yologram.api.domain.bms.exception.UserNotFoundException
import link.yologram.api.domain.bms.model.DeleteBoardResponse
import link.yologram.api.domain.bms.model.board.BoardDataWithMetrics
import link.yologram.api.domain.bms.repository.board.BoardRepository
import link.yologram.api.domain.ums.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class BoardServiceTest() {

    @Mock
    private lateinit var boardRepository: BoardRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var service: BoardService

    @Nested
    @DisplayName("게시글 작성 테스트")
    inner class CreateBoard {

        @Test
        @DisplayName("게시글 작성에 성공한다")
        fun `게시글 작성에 성공한다`() {

            // Given
            val uid: Long = 100
            val title = "dummy title"
            val body = "dummy body"

            BDDMockito.given(
                userRepository.existsById(any())
            ).willReturn(true)

            BDDMockito.given(
                boardRepository.save(any())
            ).willReturn(
                Board(
                    uid = uid,
                    title = title,
                    body = body
                ).apply {
                    createdDate = LocalDateTime.now()
                    modifiedDate = LocalDateTime.now()
                }
            )

            // When, Then
            val result = service.createBoard(uid = uid, title = title, body = body)
            assertThat(result.data.body).isEqualTo(body)
        }

        @Test
        @DisplayName("게시글 작성 실패 시 UserNotFoundException를 throw 한다")
        fun `게시글 작성 실패 시 UserNotFoundException를 throw 한다`() {

            // Given
            val uid: Long = 100
            val title = "dummy title"
            val body = "dummy body"

            BDDMockito.given(
                userRepository.existsById(any())
            ).willReturn(false)


            // When & Then
            assertThatThrownBy {
                service.createBoard(uid = uid, title = title, body = body)
            }.isExactlyInstanceOf(UserNotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("게시글 삭제 테스트")
    inner class DeleteBoard {

        @Test
        @DisplayName("게시글 삭제에 성공한다")
        fun `게시글 삭제에 성공한다`() {
            val uid = 1L
            val bid = 10L

            BDDMockito.given(userRepository.existsById(uid)).willReturn(true)
            BDDMockito.given(boardRepository.findById(bid)).willReturn(Optional.of(Board(id = bid, uid = uid, title = "title", body = "body")))
            BDDMockito.willDoNothing().given(boardRepository).deleteById(bid)

            val result = service.deleteBoard(uid, bid)

            assertThat(result.data).isInstanceOf(DeleteBoardResponse::class.java)
            assertThat(result.data.uid).isEqualTo(uid)
            assertThat(result.data.bid).isEqualTo(bid)
        }

        @Test
        @DisplayName("User가 존재하지 않을 때 UserNotFoundException를 throw한다")
        fun `User가 존재하지 않을 때 UserNotFoundException를 throw한다`() {
            val uid = 1L
            val bid = 10L

            BDDMockito.given(userRepository.existsById(uid)).willReturn(false)

            assertThatThrownBy { service.deleteBoard(uid, bid) }
                .isExactlyInstanceOf(UserNotFoundException::class.java)
        }

        @Test
        @DisplayName("User가 존재하지 않을 때 UserNotFoundException를 throw한다")
        fun `User가 존재하지 않을 때 UserNotFoundException를 throw한다 `() {
            val uid = 1L
            val bid = 10L

            BDDMockito.given(userRepository.existsById(uid)).willReturn(true)
            BDDMockito.given(boardRepository.findById(bid)).willReturn(Optional.empty())

            assertThatThrownBy { service.deleteBoard(uid, bid) }
                .isExactlyInstanceOf(BoardNotFoundException::class.java)
        }

        @Test
        @DisplayName("Board 작성자가 아닌 경우 BoardWrongWriterException를 throw한다")
        fun `Board 작성자가 아닌 경우 BoardWrongWriterException를 throw한다`() {
            val uid = 1L
            val bid = 10L
            val otherUserBoard = Board(id = bid, uid = 2L, title = "title", body = "body")

            BDDMockito.given(userRepository.existsById(uid)).willReturn(true)
            BDDMockito.given(boardRepository.findById(bid)).willReturn(Optional.of(otherUserBoard))

            assertThatThrownBy { service.deleteBoard(uid, bid) }
                .isExactlyInstanceOf(BoardWrongWriterException::class.java)
        }
    }

    @Nested
    @DisplayName("단일 게시글 조회 테스트")
    inner class GetBoardTest {

        @Test
        @DisplayName("게시글 단일 조회에 성공한다")
        fun `게시글 단일 조회에 성공한다`() {
            val bid = 10L
            val board = BoardDataWithMetrics(
                bid = bid,
                uid = 1L,
                title = "test title",
                body = "test body",
                metrics = BoardDataWithMetrics.Metrics(
                    commentCount = 5,
                    likeCount = 3,
                    viewCount = 8
                ),
                createdDate = LocalDateTime.now(),
                modifiedDate = LocalDateTime.now()
            )

            BDDMockito.given(boardRepository.findBoardWithMetricsById(bid)).willReturn(board)

            val result = service.getBoard(bid)
            assertThat(result.data).isEqualTo(board)
        }

        @Test
        @DisplayName("게시글이 없을 경우 null을 반환한다")
        fun `게시글이 없을 경우 null을 반환한다`() {
            val bid = 99L
            BDDMockito.given(boardRepository.findBoardWithMetricsById(bid)).willReturn(null)

            val result = service.getBoard(bid)
            assertThat(result.data).isNull()
        }
    }

    @Nested
    @DisplayName("커서 기반 게시글 목록 조회 테스트")
    inner class GetBoardsWithMetricsTest {

        @Test
        @DisplayName("커서 기반 게시글 목록을 정상 조회한다")
        fun `커서 기반 게시글 목록을 정상 조회한다`() {
            val bid1 = 101L
            val bid2 = 102L
            val boardList = listOf(
                BoardDataWithMetrics(
                    bid = bid1,
                    uid = 1L,
                    title = "Title1",
                    body = "Body1",
                    createdDate = LocalDateTime.now(),
                    modifiedDate = LocalDateTime.now(),
                    metrics = BoardDataWithMetrics.Metrics(
                        commentCount = 5,
                        likeCount = 3,
                        viewCount = 8
                    ),
                ),
                BoardDataWithMetrics(
                    bid = bid2,
                    uid = 2L,
                    title = "Title2",
                    body = "Body2",
                    createdDate = LocalDateTime.now(),
                    modifiedDate = LocalDateTime.now(),
                    metrics = BoardDataWithMetrics.Metrics(
                        commentCount = 5,
                        likeCount = 3,
                        viewCount = 1
                    ),
                )
            )

            val cursor = BoardService.CursorUtil.encode(100L)

            BDDMockito.given(boardRepository.findBoardsWithMetrics(100L, 2)).willReturn(boardList)

            val result = service.getBoardsWithMetrics(cursor, 2)

            assertThat(result.data).hasSize(2)
            assertThat(result.data[0].bid).isEqualTo(bid1)
            assertThat(result.nextCursor).isEqualTo(BoardService.CursorUtil.encode(bid2))
        }

        @Test
        @DisplayName("마지막 커서가 없을 경우 nextCursor는 null이다")
        fun `마지막 커서가 없을 경우 nextCursor는 null이다`() {
            val boardList = emptyList<BoardDataWithMetrics>()

            val cursor = BoardService.CursorUtil.encode(100L)
            BDDMockito.given(boardRepository.findBoardsWithMetrics(100L, 2)).willReturn(boardList)

            val result = service.getBoardsWithMetrics(cursor, 2)

            assertThat(result.data).isEmpty()
            assertThat(result.nextCursor).isNull()
        }
    }

    @Nested
    @DisplayName("유저별 게시글 페이지 조회 테스트")
    inner class GetBoardsByUidTest {

        @Test
        fun `유저의 게시글 목록을 페이지로 조회한다`() {
            val uid = 1L
            val page = 0L
            val size = 2L
            val offset = page * size

            val boardList = listOf(
                BoardDataWithMetrics(
                    bid = 1L,
                    uid = uid,
                    title = "Post 1",
                    body = "Body 1",
                    createdDate = LocalDateTime.now(),
                    modifiedDate = LocalDateTime.now(),
                    metrics = BoardDataWithMetrics.Metrics(
                        commentCount = 5,
                        likeCount = 3,
                        viewCount = 1
                    ),
                ),
                BoardDataWithMetrics(
                    bid = 2L,
                    uid = uid,
                    title = "Post 2",
                    body = "Body 2",
                    createdDate = LocalDateTime.now(),
                    modifiedDate = LocalDateTime.now(),
                    metrics = BoardDataWithMetrics.Metrics(
                        commentCount = 5,
                        likeCount = 3,
                        viewCount = 1
                    ),
                )
            )

            BDDMockito.given(boardRepository.findBoardsWithMetricsByUid(uid, size, offset)).willReturn(boardList)
            BDDMockito.given(boardRepository.countBoardsByUid(uid)).willReturn(4L)

            val result = service.getBoardsByUid(uid, page, size)

            assertThat(result.page).isEqualTo(0L)
            assertThat(result.size).isEqualTo(2L)
            assertThat(result.totalPages).isEqualTo(2L)
            assertThat(result.totalCount).isEqualTo(4L)
            assertThat(result.data).hasSize(2)
            assertThat(result.first).isTrue()
            assertThat(result.last).isFalse()
        }

        @Test
        fun `게시글이 없는 경우 빈 목록과 totalPages 1 반환`() {
            val uid = 1L
            val page = 0L
            val size = 5L

            BDDMockito.given(boardRepository.findBoardsWithMetricsByUid(uid, size, 0)).willReturn(emptyList())
            BDDMockito.given(boardRepository.countBoardsByUid(uid)).willReturn(0L)

            val result = service.getBoardsByUid(uid, page, size)

            assertThat(result.data).isEmpty()
            assertThat(result.totalPages).isEqualTo(1)
            assertThat(result.first).isTrue()
            assertThat(result.last).isTrue()
        }
    }

    @Nested
    @DisplayName("커서 유틸 테스트")
    inner class CursorUtilTest {

        @Test
        @DisplayName("커서 인코딩과 디코딩이 정상적으로 동작한다")
        fun `커서 인코딩과 디코딩이 정상적으로 동작한다`() {
            val originalId = 123L
            val encoded = BoardService.CursorUtil.encode(originalId)
            val decoded = BoardService.CursorUtil.decode(encoded)

            assertThat(decoded).isEqualTo(originalId)
        }

        @Test
        @DisplayName("유효하지 않은 커서를 decode하면 null을 반환한다")
        fun `유효하지 않은 커서를 decode하면 null을 반환한다`() {
            val invalidCursor = Base64.getUrlEncoder().encodeToString("invalid_prefix:456".toByteArray(Charsets.UTF_8))
            val result = BoardService.CursorUtil.decode(invalidCursor)
            assertThat(result).isNull()
        }

        @Test
        @DisplayName("base64 디코딩 실패 시 null을 반환한다")
        fun `base64 디코딩 실패 시 null을 반환한다`() {
            val brokenCursor = "this_is_not_base64"
            val result = BoardService.CursorUtil.decode(brokenCursor)
            assertThat(result).isNull()
        }
    }

}