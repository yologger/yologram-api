package link.yologram.api.domain.bms.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class RecordBoardViewRequest(

    @field:Schema(description = "유저 id", defaultValue = "", required = false)
    @field:Positive
    val uid: Long
)
