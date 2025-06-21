package link.yologram.api.domain.bms.service

import any
import link.yologram.api.domain.bms.entity.Board
import link.yologram.api.domain.bms.entity.BoardComment
import link.yologram.api.domain.bms.enum.BoardStatus
import link.yologram.api.domain.bms.exception.BoardCommentMismatchException
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.CommentNotFoundException
import link.yologram.api.domain.bms.repository.BoardCommentRepository
import link.yologram.api.domain.bms.repository.board.BoardRepository
import link.yologram.api.global.model.APIEnvelop
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class BoardCommentServiceTest {

    @Mock
    private lateinit var boardRepository: BoardRepository

    @Mock
    private lateinit var boardCommentRepository: BoardCommentRepository

    @Mock
    private lateinit var boardCommentCountService: BoardCommentCountService

    @InjectMocks
    private lateinit var boardCommentService: BoardCommentService


    @Test
    @DisplayName("댓글 작성에 성공한다")
    fun `댓글 작성에 성공한다`() {
        val boardId = 1L
        val userId = 2L
        val content = "Test comment"
        val board = Board(uid = userId, title = "title", body = "body", status = BoardStatus.ACTIVE, deletedDate = LocalDateTime.now()).apply {
            createdDate = LocalDateTime.now()
            modifiedDate = LocalDateTime.now()
        }

        val comment = BoardComment(id = 10L, bid = boardId, uid = userId, content = content).apply {
            createdDate = LocalDateTime.now()
            modifiedDate = LocalDateTime.now()
        }

        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))
        `when`(boardCommentRepository.save(any())).thenReturn(comment)
        `when`(boardCommentCountService.increaseCount(any())).thenReturn(APIEnvelop(data = 3))

        val result = boardCommentService.createComment(boardId, userId, content)
        assert(result.data.id == 10L)
    }

    @Test
    @DisplayName("게시글이 없을 때 댓글 작성에 실패한다")
    fun `게시글이 없을 때 댓글 작성에 실패한다`() {
        val boardId = 1L
        val userId = 2L
        val content = "Test comment"

        `when`(boardRepository.findById(boardId)).thenReturn(Optional.empty())

        try {
            boardCommentService.createComment(boardId, userId, content)
            assert(false) { "예외가 발생해야 함" }
        } catch (e: Exception) {
            assert(e is BoardNotFoundException)
        }
    }

    @Test
    @DisplayName("댓글 삭제에 성공한다")
    fun `댓글 삭제에 성공한다`() {
        val boardId = 1L
        val commentId = 100L
        val board = Board(id = boardId, uid = 1L, title = "title", body = "body")
        val comment = BoardComment(id = commentId, bid = boardId, uid = 1L, content = "comment")

        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))
        `when`(boardCommentRepository.findById(commentId)).thenReturn(Optional.of(comment))
        doNothing().`when`(boardCommentRepository).deleteById(commentId)

        boardCommentService.deleteComment(boardId, commentId)
    }

    @Test
    @DisplayName("게시글이 없는 경우, 댓글 삭제에 실패한다")
    fun `게시글이 없는 경우, 댓글 삭제에 실패한다`() {
        val boardId = 1L
        val commentId = 100L
        `when`(boardRepository.findById(boardId)).thenReturn(Optional.empty())

        try {
            boardCommentService.deleteComment(boardId, commentId)
            assert(false) { "예외가 발생해야 함" }
        } catch (e: Exception) {
            assert(e is BoardNotFoundException)
        }
    }

    @Test
    @DisplayName("없는 댓글을 삭제하려는 경우 실패한다")
    fun `없는 댓글을 삭제하려는 경우 실패한다`() {
        val boardId = 1L
        val commentId = 100L
        val board = Board(id = boardId, uid = 1L, title = "title", body = "body")

        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))
        `when`(boardCommentRepository.findById(commentId)).thenReturn(Optional.empty())

        try {
            boardCommentService.deleteComment(boardId, commentId)
            assert(false) { "예외가 발생해야 함" }
        } catch (e: Exception) {
            assert(e is CommentNotFoundException)
        }
    }

    @Test
    @DisplayName("삭제하려는 댓글이 게시글과 다른 경우 예외가 발생한다")
    fun `삭제하려는 댓글이 게시글과 다른 경우 예외가 발생한다`() {
        val boardId = 1L
        val commentId = 100L
        val board = Board(id = boardId, uid = 1L, title = "title", body = "body")
        val comment = BoardComment(id = commentId, bid = 999L, uid = 1L, content = "comment")

        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))
        `when`(boardCommentRepository.findById(commentId)).thenReturn(Optional.of(comment))

        try {
            boardCommentService.deleteComment(boardId, commentId)
            assert(false) { "예외가 발생해야 함" }
        } catch (e: Exception) {
            assert(e is BoardCommentMismatchException)
        }
    }

    @Test
    @DisplayName("Board id로 Comment 들을 조회하는데 성공한다")
    fun `board id로 Comment 목록을 조회하는데 성공한다`() {
        val boardId = 1L
        val board = Board(id = boardId, uid = 1L, title = "title", body = "body", status = BoardStatus.ACTIVE, deletedDate = null).apply {
            createdDate = LocalDateTime.now()
            modifiedDate = LocalDateTime.now()
        }
        val comments = listOf(BoardComment(id = 10L, bid = boardId, uid = 1L, content = "comment").apply {
            createdDate = LocalDateTime.now()
            modifiedDate = LocalDateTime.now()
        })

        `when`(boardRepository.findById(boardId)).thenReturn(Optional.of(board))
        `when`(boardCommentRepository.findAllByBid(boardId)).thenReturn(comments)

        val result = boardCommentService.getCommentsByBid(boardId)
        assert(result.data.size == 1)
    }

    @Test
    @DisplayName("게시글이 없는 경우, board id로 comment 조회에 실패한다")
    fun `게시글이 없는 경우, board id로 comment 조회에 실패한다`() {
        val boardId = 1L
        `when`(boardRepository.findById(boardId)).thenReturn(Optional.empty())

        try {
            boardCommentService.getCommentsByBid(boardId)
            assert(false) { "예외가 발생해야 함" }
        } catch (e: Exception) {
            assert(e is BoardNotFoundException)
        }
    }
}