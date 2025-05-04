package link.yologram.api.domain.bms.model

import jakarta.validation.constraints.Positive

data class DeleteBoardRequest(
    @field:Positive
    val uid: Long,

    @field:Positive
    val bid: Long,
)
