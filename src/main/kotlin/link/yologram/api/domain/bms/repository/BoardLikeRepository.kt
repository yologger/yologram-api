package link.yologram.api.domain.bms.repository

import link.yologram.api.domain.bms.entity.BoardLike
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BoardLikeRepository: JpaRepository<BoardLike, Long> {
    fun findByUidAndBid(uid: Long, bid: Long): Optional<BoardLike>
}