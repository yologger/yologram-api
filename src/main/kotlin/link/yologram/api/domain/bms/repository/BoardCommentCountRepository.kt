package link.yologram.api.domain.bms.repository

import link.yologram.api.domain.bms.entity.BoardCommentCount
import link.yologram.api.domain.bms.entity.BoardLikeCount
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface BoardCommentCountRepository: JpaRepository<BoardCommentCount, Long> {
    fun findByBid(bid: Long): Optional<BoardCommentCount>
}