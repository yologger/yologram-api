package link.yologram.api.domain.bms.model

import link.yologram.api.domain.bms.model.board.BoardDataWithMetrics

data class GetBoardsResponse(
    val size: Int,
    val boards: List<BoardDataWithMetrics>
)
