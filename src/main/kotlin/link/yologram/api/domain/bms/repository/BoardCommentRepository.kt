package link.yologram.api.domain.bms.repository

import link.yologram.api.domain.bms.entity.BoardComment
import org.springframework.data.jpa.repository.JpaRepository

interface BoardCommentRepository : JpaRepository<BoardComment, Long> {
    fun findAllByBid(bid: Long): List<BoardComment>
}