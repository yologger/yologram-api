package link.yologram.api.domain.bms.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class EditBoardRequest(
    @field:Positive
    val uid: Long,

    @field:Positive
    val bid: Long,

    @field:NotBlank(message = "'title' must not be empty.")
    @field:Size(min = 4, max = 256, message = "'title' length must be between 4 and 256.")
    val title: String,

    @field:NotBlank(message = "'body' must not be empty.")
    @field:Size(min = 4, message = "'body' length must be longer than or equal to 4")
    val body: String
)
