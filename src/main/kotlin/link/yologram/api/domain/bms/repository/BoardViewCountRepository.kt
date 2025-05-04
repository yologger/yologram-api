package link.yologram.api.domain.bms.repository

import link.yologram.api.domain.bms.entity.BoardLike
import link.yologram.api.domain.bms.entity.BoardViewCount
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BoardViewCountRepository: JpaRepository<BoardViewCount, Long>