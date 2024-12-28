package link.yologram.api.domain.ums.dto

import java.time.LocalDateTime

data class WithdrawResponse(
    val uid: Long,
    val deletedAt: LocalDateTime?,
    val deletedBoardsCount: Int
)
