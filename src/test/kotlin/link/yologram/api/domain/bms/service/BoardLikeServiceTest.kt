package link.yologram.api.domain.bms.service

import junit.framework.TestCase.assertEquals
import link.yologram.api.domain.bms.entity.Board
import link.yologram.api.domain.bms.entity.BoardLike
import link.yologram.api.domain.bms.entity.BoardLikeCount
import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.UserAlreadyLikeBoardException
import link.yologram.api.domain.bms.exception.UserNotLikeBoardException
import link.yologram.api.domain.bms.repository.BoardLikeCountRepository
import link.yologram.api.domain.bms.repository.BoardLikeRepository
import link.yologram.api.domain.bms.repository.board.BoardRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class BoardLikeServiceTest {

    @Mock
    private lateinit var boardRepository: BoardRepository

    @Mock
    private lateinit var boardLikeRepository: BoardLikeRepository

    @Mock
    private lateinit var boardLikeCountRepository: BoardLikeCountRepository

    @InjectMocks
    private lateinit var boardLikeService: BoardLikeService

    @Nested
    @DisplayName("게시글 좋아요 테스트")
    inner class LikeBoardTest {

        @Test
        @DisplayName("게시글 좋아요에 성공한다")
        fun `게시글 좋아요에 성공한다`() {
            val uid = 1L
            val bid = 10L
            val board = Board(id = bid, uid = uid, title = "title", body = "body")
            val like = BoardLike(id = 0L, uid = uid, bid = bid)
            val likeCount = BoardLikeCount(bid = bid, count = 0)

            val willReturn = given(boardRepository.findById(bid)).willReturn(Optional.of(board))
            given(boardLikeRepository.findByUidAndBid(uid, bid)).willReturn(Optional.of(like))
            given(boardLikeCountRepository.findByBid(bid)).willReturn(Optional.of(likeCount))

            val result = boardLikeService.likeBoard(uid, bid)

            assertEquals(uid, result.data.uid)
            assertEquals(bid, result.data.bid)
        }

        @Test
        @DisplayName("이미 좋아요에 성공한 경우 게시글 좋아요에 실패한다.")
        fun `이미 좋아요에 성공한 경우 게시글 좋아요에 실패한다`() {
            val uid = 1L
            val bid = 10L
            val board = Board(id = bid, uid = uid, title = "title", body = "body")
            val existingLike = BoardLike(id = 100L, uid = uid, bid = bid)

            given(boardRepository.findById(bid)).willReturn(Optional.of(board))
            given(boardLikeRepository.findByUidAndBid(uid, bid)).willReturn(Optional.of(existingLike))

            // When & Then
            assertThatThrownBy {
                boardLikeService.likeBoard(uid, bid)
            }.isExactlyInstanceOf(UserAlreadyLikeBoardException::class.java)
        }


        @Test
        @DisplayName("게시글이 없는 경우 게시글 좋아요에 실패한다")
        fun `게시글이 없는 경우 게시글 좋아요에 실패한다`() {
            val uid = 1L
            val bid = 10L

            given(boardRepository.findById(bid)).willReturn(Optional.empty())

            // When & Then
            assertThatThrownBy {
                boardLikeService.likeBoard(uid, bid)
            }.isExactlyInstanceOf(BoardNotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("게시글 좋아요 취소 테스트")
    inner class UnlikeBoardTest {

        @Test
        @DisplayName("게시글 좋아요 취소에 성공한다")
        fun `게시글 좋아요 취소에 성공한다`() {
            val uid = 1L
            val bid = 10L
            val board = Board(id = bid, uid = uid, title = "title", body = "body")
            val like = BoardLike(id = 123L, uid = uid, bid = bid)
            val likeCount = BoardLikeCount(bid = bid, count = 2)

            given(boardRepository.findById(bid)).willReturn(Optional.of(board))
            given(boardLikeRepository.findByUidAndBid(uid, bid)).willReturn(Optional.of(like))
            given(boardLikeCountRepository.findByBid(bid)).willReturn(Optional.of(likeCount))

            val response = boardLikeService.unlikeBoard(uid, bid)
            assertThat(response.data.uid).isEqualTo(uid)
            assertThat(response.data.bid).isEqualTo(bid)
        }

        @Test
        @DisplayName("게시글이 없을 경우, 좋아요 취소에 실패한다")
        fun `게시글이 없을 경우, 좋아요 취소에 실패한다`() {
            val uid = 1L
            val bid = 10L
            given(boardRepository.findById(bid)).willReturn(Optional.empty())

            // When & Then
            assertThatThrownBy {
                boardLikeService.unlikeBoard(uid, bid)
            }.isExactlyInstanceOf(BoardNotFoundException::class.java)
        }


        @Test
        @DisplayName("이전 좋아요 정보가 없는 경우, 게시글 좋아요 취소에 실패한다")
        fun `이전 좋아요 정보가 없는 경우, 게시글 좋아요 취소에 실패한다`() {
            val uid = 1L
            val bid = 10L
            val board = Board(id = bid, uid = uid, title = "title", body = "body")

            given(boardRepository.findById(bid)).willReturn(Optional.of(board))
            given(boardLikeRepository.findByUidAndBid(uid, bid)).willReturn(Optional.empty())

            // When & Then
            assertThatThrownBy {
                boardLikeService.unlikeBoard(uid, bid)
            }.isExactlyInstanceOf(UserNotLikeBoardException::class.java)
        }
    }
}