package link.yologram.api.domain.bms.repository

import link.yologram.api.domain.bms.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByBid(bid: Long): List<Comment>
}