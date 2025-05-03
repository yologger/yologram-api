package link.yologram.api.domain.bms.dto

import link.yologram.api.domain.bms.dto.board.BoardDataWithMetrics

data class GetBoardsResponse(
    val size: Int,
    val boards: List<BoardDataWithMetrics>
)
