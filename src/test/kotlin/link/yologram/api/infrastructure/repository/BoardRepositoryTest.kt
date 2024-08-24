package link.yologram.api.infrastructure.repository

import link.yologram.api.infrastructure.entity.Board
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.jdbc.Sql

// @Disabled
@Sql(scripts = ["/sql/repository/insert_bulk_users.sql", "/sql/repository/insert_bulk_boards.sql"])
class BoardRepositoryTest(
    @Autowired private val boardRepository: BoardRepository
): AbstractDataJpaTest() {
    @Test
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
    fun `Uid 기반 Boards 조회 `() {
        val size = 5L
        val boards = boardRepository.findBoardsByUidOrderByCreateDateDesc(uid = 1, page = 0, size = size);
        assertThat(boards.size).isEqualTo(size)
    }

    @Test
    fun `최근 Boards 조회`() {
        val size = 5
        val sort = Sort.by("id").descending()
        val pageable = PageRequest.of(0, size, sort)
        val boards = boardRepository.findAll(pageable)
        assertThat(boards.size).isEqualTo(size)
    }
}