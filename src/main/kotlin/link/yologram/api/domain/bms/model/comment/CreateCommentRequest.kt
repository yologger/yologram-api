package link.yologram.api.domain.bms.model.comment

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(
    description = "댓글 생성을 위한 request 모델"
)
data class CreateCommentRequest(

    @Schema(description = "댓글 내용", defaultValue = "", required = true)
    @field:NotBlank(message = "'content' must not be empty.")
    @field:Size(min = 4, message = "'content' length must be longer than or equal to 4")
    val content: String
)