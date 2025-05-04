package link.yologram.api.domain.bms.model

import link.yologram.api.domain.bms.model.board.BoardData

data class GetBoardsByUidResponse(
    val size: Int,
    val boards: List<BoardData>
)
