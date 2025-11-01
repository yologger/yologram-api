package link.yologram.api.domain.bms.repository

import link.yologram.api.common.AbstractRepositoryDataJpaTest
import link.yologram.api.domain.bms.repository.board.BoardRepository
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
    @DisplayName("최근 Boards 5개 조회")
    fun `최근 Boards 5개 조회`() {
        val size = 5
        val sort = Sort.by("id").descending()
        val pageable = PageRequest.of(0, size, sort)
        val boards = boardRepository.findAll(pageable)
        assertThat(boards.size).isEqualTo(size)
    }

    @Test
    @DisplayName("BoardWithMetrics 단건 조회")
    fun `BoardWithMetrics 단건 조회`() {
        val board = boardRepository.findBoardWithMetricsById(1)
        assertThat(board).isNotNull()
        assertThat(board?.metrics?.commentCount).isEqualTo(2)
        assertThat(board?.metrics?.likeCount).isEqualTo(3)
        assertThat(board?.metrics?.viewCount).isEqualTo(4)
    }

    @Test
    @DisplayName("Board 다건 조회")
    fun `Board 다건 조회`() {
        val pageSize: Long = 5

        var nextCursorId: Long? = null
        var boards = boardRepository.findBoards(cursorId = nextCursorId, pageSize = pageSize)
        assertThat(boards.size).isEqualTo(pageSize)
        assertThat(boards[0].title).isEqualTo("user3 title5")

        nextCursorId = boards.lastOrNull()?.bid
        boards = boardRepository.findBoards(cursorId = nextCursorId, pageSize = pageSize)
        assertThat(boards.size).isEqualTo(pageSize)
        assertThat(boards[0].title).isEqualTo("user2 title5")

        nextCursorId = boards.lastOrNull()?.bid
        boards = boardRepository.findBoards(cursorId = nextCursorId, pageSize = pageSize)
        assertThat(boards.size).isEqualTo(4)
        assertThat(boards[0].title).isEqualTo("user1 title4")

        nextCursorId = boards.lastOrNull()?.bid
        boards = boardRepository.findBoards(cursorId = nextCursorId, pageSize = pageSize)
        assertThat(boards.size).isEqualTo(0)
    }

    @Test
    @DisplayName("BoardWithMetrics 다건 조회")
    fun `BoardWithMetrics 다건 조회`() {
        val pageSize: Long = 5

        var nextCursorId: Long? = null
        var boards = boardRepository.findBoardsWithMetrics(cursorId = nextCursorId, pageSize = pageSize)
        assertThat(boards.size).isEqualTo(pageSize)
        assertThat(boards[0].title).isEqualTo("user3 title5")

        nextCursorId = boards.lastOrNull()?.bid
        boards = boardRepository.findBoardsWithMetrics(cursorId = nextCursorId, pageSize = pageSize)
        assertThat(boards.size).isEqualTo(pageSize)
        assertThat(boards[0].title).isEqualTo("user2 title5")

        nextCursorId = boards.lastOrNull()?.bid
        boards = boardRepository.findBoardsWithMetrics(cursorId = nextCursorId, pageSize = pageSize)
        assertThat(boards.size).isEqualTo(4)
        assertThat(boards[0].title).isEqualTo("user1 title4")

        nextCursorId = boards.lastOrNull()?.bid
        boards = boardRepository.findBoardsWithMetrics(cursorId = nextCursorId, pageSize = pageSize)
        assertThat(boards.size).isEqualTo(0)
    }

    @Test
    @DisplayName("Uid로 게시글 수 조회")
    fun `Uid로 게시글 수 조회`() {
        var count = boardRepository.countBoardsByUid(uid = 1)
        assertThat(count).isEqualTo(4)

        count = boardRepository.countBoardsByUid(uid = 2)
        assertThat(count).isEqualTo(5)

        count = boardRepository.countBoardsByUid(uid = 5)
        assertThat(count).isEqualTo(0)
    }

    @Test
    @DisplayName("Uid로 BoardWithMetrics 다건 조회")
    fun `Uid로 BoardWithMetrics 다건 조회`() {

        var uid: Long = 2
        var page: Long = 0
        var size : Long= 3
        var offset = page * size

        var boards = boardRepository.findBoardsWithMetricsByUid(uid, size, offset)
        assertThat(boards.size).isEqualTo(3)

        page ++
        offset = page * size
        boards = boardRepository.findBoardsWithMetricsByUid(uid, size, offset)
        assertThat(boards.size).isEqualTo(2)

        page ++
        offset = page * size
        boards = boardRepository.findBoardsWithMetricsByUid(uid, size, offset)
        assertThat(boards.size).isEqualTo(0)
    }

    @Test
    @DisplayName("ID 범위로 BoardWithMetrics 다건 조회")
    fun `ID 범위로 BoardWithMetrics 다건 조회`() {

        // 범위 내 조회
        var boards = boardRepository.findBoardsWithMetrics(from = 1, to = 5)
        assertThat(boards.size).isEqualTo(5)
        assertThat(boards[0].bid).isEqualTo(1)
        assertThat(boards[4].bid).isEqualTo(5)
        assertThat(boards[0].metrics).isNotNull()

        // 존재하지 않는 범위 조회
        boards = boardRepository.findBoardsWithMetrics(from = 9999, to = 10000)
        assertThat(boards.size).isEqualTo(0)
    }

}