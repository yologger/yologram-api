package link.yologram.api.domain.bms.service

import any
import link.yologram.api.domain.bms.entity.BoardCommentCount
import link.yologram.api.domain.bms.repository.BoardCommentCountRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class BoardCommentCountServiceTest() {

    @Mock
    private lateinit var boardCommentCountRepository: BoardCommentCountRepository

    @InjectMocks
    private lateinit var boardCommentCountService: BoardCommentCountService

    @Test
    @DisplayName("기존 댓글이 존재하는 경우, 댓글 수 증가 테스트")
    fun `기존 댓글이 존재하는 경우, 댓글 수 증가 테스트`() {
        val bid = 1L
        val existing = BoardCommentCount(bid = bid, count = 5)
        `when`(boardCommentCountRepository.findByBid(bid)).thenReturn(Optional.of(existing))

        val result = boardCommentCountService.increaseCount(bid)
        assert(result.data == 6L)
    }

    @Test
    @DisplayName("기존에 댓글이 존재하지 않는 경우, 댓글 수 증가 테스트")
    fun `기존에 댓글이 존재하지 않는 경우, 댓글 수 증가 테스트`() {
        val bid = 2L
        val newCount = BoardCommentCount(bid = bid, count = 0)
        `when`(boardCommentCountRepository.findByBid(bid)).thenReturn(Optional.empty())
        `when`(boardCommentCountRepository.save(any())).thenReturn(newCount)

        val result = boardCommentCountService.increaseCount(bid)
        assert(result.data == 1L)
    }

    @Test
    @DisplayName("기존 댓글이 존재하는 경우, 댓글 수 감소 테스트")
    fun `기존 댓글이 존재하는 경우, 댓글 수 감소 테스트`() {
        val bid = 3L
        val existing = BoardCommentCount(bid = bid, count = 2)
        `when`(boardCommentCountRepository.findByBid(bid)).thenReturn(Optional.of(existing))

        boardCommentCountService.decreaseCount(bid)
        assert(existing.count == 1L)
    }

    @Test
    @DisplayName("기존 댓글이 존재하지 않는 경우, 댓글 수 감소 테스트")
    fun `기존 댓글이 존재하지 않는 경우, 댓글 수 감소 테스트`() {
        val bid = 4L
        val existing = BoardCommentCount(bid = bid, count = 1)
        `when`(boardCommentCountRepository.findByBid(bid)).thenReturn(Optional.of(existing))
        doNothing().`when`(boardCommentCountRepository).delete(existing)

        boardCommentCountService.decreaseCount(bid)
        assert(existing.count == 0L)
    }
}