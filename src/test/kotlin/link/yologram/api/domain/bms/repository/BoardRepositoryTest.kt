package link.yologram.api.domain.bms.repository

import link.yologram.api.domain.bms.entity.Board
import link.yologram.api.common.AbstractRepositoryDataJpaTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class BoardRepositoryTest(
    @Autowired private val boardRepository: BoardRepository
) : AbstractRepositoryDataJpaTest() {

    @Test
    @DisplayName("Board 단건 조회")
    fun `Board 단건 조회`() {
        val board = boardRepository.findById(1)
        assertThat(board.isPresent).isTrue()
    }

    @Test
    @DisplayName("Board 단건 추가")
    fun `Board 단건 추가`() {

        // Given
        val uid = 1L
        val title = "제목입니다."
        val body = "본문입니다."

        // When
        val saved = boardRepository.save(
            Board(
                uid = uid,
                title = title,
                body = body
            )
        )

        val board = boardRepository.findById(saved.id)

        assertThat(saved.title).isEqualTo(title)
        assertThat(board.get().title).isEqualTo(title)
    }

    @Test
    @DisplayName("최근 Boards 5개 조회")
    fun `최근 Boards 5개 조회`() {
        val size = 5
        val sort = Sort.by("id").descending()
        val pageable = PageRequest.of(0, size, sort)
        val boards = boardRepository.findAll(pageable)
        assertThat(boards.size).isEqualTo(size)
    }
}