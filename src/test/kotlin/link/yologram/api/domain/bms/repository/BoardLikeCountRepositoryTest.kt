package link.yologram.api.domain.bms.repository

import link.yologram.api.common.AbstractRepositoryDataJpaTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BoardLikeCountRepositoryTest(
    @Autowired private val boardLikeCountRepository: BoardLikeCountRepository,
) : AbstractRepositoryDataJpaTest() {

    @Test
    @DisplayName("BoardLikeCount 조회")
    fun `BoardLikeCount 조회`() {

        val bid = 1L
        val count = boardLikeCountRepository.findByBid(bid)
        assertThat(count?.get()?.count).isEqualTo(3)
    }
}