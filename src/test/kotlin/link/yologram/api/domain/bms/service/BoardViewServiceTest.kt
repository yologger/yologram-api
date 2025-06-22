package link.yologram.api.domain.bms.service

import link.yologram.api.domain.bms.exception.BoardNotFoundException
import link.yologram.api.domain.bms.exception.UserNotFoundException
import link.yologram.api.domain.bms.repository.BoardViewCountRepository
import link.yologram.api.domain.bms.repository.BoardViewEventRepository
import link.yologram.api.domain.bms.repository.board.BoardRepository
import link.yologram.api.domain.ums.repository.UserRepository
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class BoardViewServiceTest {

    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val boardRepository: BoardRepository = mock(BoardRepository::class.java)
    private val boardViewEventRepository: BoardViewEventRepository = mock(BoardViewEventRepository::class.java)
    private val boardViewCountRepository: BoardViewCountRepository = mock(BoardViewCountRepository::class.java)

    private val boardViewService = BoardViewService(
        userRepository,
        boardRepository,
        boardViewEventRepository,
        boardViewCountRepository
    )

    @Nested
    @DisplayName("조회수 기록 테스트")
    inner class RecordViewTest {

        @Test
        @DisplayName("정상 조회 기록 시 저장 및 증가한다")
        fun `정상 조회 기록 시 저장 및 증가한다`() {
            val uid = 1L
            val bid = 100L
            val ip = "127.0.0.1"

            `when`(userRepository.existsById(uid)).thenReturn(true)
            `when`(boardRepository.existsById(bid)).thenReturn(true)
            `when`(boardViewEventRepository.existsByBidAndUidAndIp(bid, uid, ip)).thenReturn(false)
            `when`(boardViewCountRepository.findById(bid)).thenReturn(Optional.empty())

            assertDoesNotThrow {
                boardViewService.recordView(bid, uid, ip)
            }
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 예외 발생한다")
        fun `유저가 존재하지 않으면 예외 발생한다`() {
            val uid = 1L
            val bid = 100L
            val ip = "127.0.0.1"

            `when`(userRepository.existsById(uid)).thenReturn(false)

            // When & Then
            assertThatThrownBy {
                boardViewService.recordView(bid, uid, ip)
            }.isExactlyInstanceOf(UserNotFoundException::class.java)
        }

        @Test
        @DisplayName("게시글이 존재하지 않으면 예외 발생한다")
        fun `게시글이 존재하지 않으면 예외 발생한다`() {
            val uid = 1L
            val bid = 100L
            val ip = "127.0.0.1"

            `when`(userRepository.existsById(uid)).thenReturn(true)
            `when`(boardRepository.existsById(bid)).thenReturn(false)

            // When & Then
            assertThatThrownBy {
                boardViewService.recordView(bid, uid, ip)
            }.isExactlyInstanceOf(BoardNotFoundException::class.java)
        }

        @Test
        @DisplayName("이미 조회한 경우 저장하지 않는다")
        fun `이미 조회한 경우 저장하지 않는다`() {
            val uid = 1L
            val bid = 100L
            val ip = "127.0.0.1"

            `when`(userRepository.existsById(uid)).thenReturn(true)
            `when`(boardRepository.existsById(bid)).thenReturn(true)
            `when`(boardViewEventRepository.existsByBidAndUidAndIp(bid, uid, ip)).thenReturn(true)

            boardViewService.recordView(bid, uid, ip)

            verify(boardViewEventRepository, never()).save(any())
            verify(boardViewCountRepository, never()).save(any())
        }
    }
}