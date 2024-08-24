package link.yologram.api.domain.bms.dto

import jakarta.validation.constraints.Positive

data class DeleteBoardRequest(
    @field:Positive
    val uid: Long,

    @field:Positive
    val bid: Long,
)
