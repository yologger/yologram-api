package link.yologram.api.domain.bms.repository

import link.yologram.api.common.AbstractRepositoryDataJpaTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BoardCommentCountRepositoryTest(
    @Autowired private val boardCommentCountRepository: BoardCommentCountRepository
) : AbstractRepositoryDataJpaTest() {

    @Test
    @DisplayName("BoardCommentCount 조회")
    fun `BoardCommentCount 조회`() {

        val bid = 1L
        val count = boardCommentCountRepository.findByBid(bid)
        assertThat(count?.get()?.count).isEqualTo(2)
    }
}