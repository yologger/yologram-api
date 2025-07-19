package link.yologram.api.domain.bms.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

@Schema(
    description = "게시글 생성을 위한 request 모델"
)
data class CreateBoardRequest(

    @Schema(description = "유저 id", defaultValue = "", required = true)
    @field:Positive
    val uid: Long,

    @Schema(description = "게시글 제목", defaultValue = "", required = true, example = "yologger1013@gmai.com")
    @field:NotBlank(message = "'title' must not be empty.")
    @field:Size(min = 4, max = 256, message = "'title' length must be between 4 and 256.")
    val title: String,

    @Schema(description = "게시글 내용", defaultValue = "", required = true)
    @field:NotBlank(message = "'content' must not be empty.")
    @field:Size(min = 4, message = "'content' length must be longer than or equal to 4")
    val content: String
)
