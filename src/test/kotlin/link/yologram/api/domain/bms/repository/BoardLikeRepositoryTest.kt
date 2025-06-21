package link.yologram.api.domain.bms.repository

import link.yologram.api.common.AbstractRepositoryDataJpaTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BoardLikeRepositoryTest(
    @Autowired private val boardLikeRepository: BoardLikeRepository,
) : AbstractRepositoryDataJpaTest() {

    @Test
    @DisplayName("BoardLike 조회 테스트")
    fun `BoardLike 조회 테스트`() {
        val boardLike = boardLikeRepository.findByUidAndBid(uid = 2L, bid = 1L)
        assertThat(boardLike.isPresent).isEqualTo(true)
    }
}