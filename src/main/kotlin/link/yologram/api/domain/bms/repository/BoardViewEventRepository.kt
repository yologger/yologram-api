package link.yologram.api.domain.bms.repository

import link.yologram.api.domain.bms.entity.BoardViewEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param;

interface BoardViewEventRepository: JpaRepository<BoardViewEvent, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(bve) > 0 THEN true ELSE false END
        FROM BoardViewEvent bve
        WHERE bve.bid = :bid
          AND ((:uid IS NOT NULL AND bve.uid = :uid) OR (:uid IS NULL AND bve.uid IS NULL AND bve.ip = :ip))
    """)
    fun existsByBidAndUidAndIp(@Param("bid") bid: Long, @Param("uid") uid: Long?, @Param("ip") ip: String?): Boolean

}