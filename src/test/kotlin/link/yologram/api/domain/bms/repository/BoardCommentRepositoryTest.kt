package link.yologram.api.domain.bms.repository

import link.yologram.api.common.AbstractRepositoryDataJpaTest
import link.yologram.api.domain.bms.entity.BoardComment
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BoardCommentRepositoryTest(
    @Autowired private val boardCommentRepository: BoardCommentRepository
) : AbstractRepositoryDataJpaTest() {

    @Test
    @DisplayName("BoardComment 추가")
    fun `Comment 추가`() {
        val uid = 1L
        val bid = 1L
        val content = "This is a content"
        val boardComment = BoardComment(
            uid = uid,
            bid = bid,
            content = content
        )
        val created = boardCommentRepository.save(boardComment)
        assertThat(created.content).isEqualTo(content)
    }

    @Test
    @DisplayName("BoardComment 다건 조회")
    fun `Comment 다건 조회`() {
        val comments = boardCommentRepository.findAllByBid(1)
        assertThat(comments.size).isEqualTo(2)
    }
}