package link.yologram.api.domain.ums.model

import java.time.LocalDateTime

data class WithdrawResponse(
    val uid: Long,
    val deletedAt: LocalDateTime?,
    val deletedBoardsCount: Int
)
