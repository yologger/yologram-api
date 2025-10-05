package link.yologram.api.domain.ums.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(
    description = "token 검증을 위한 request 모델"
)
data class ValidateAccessTokenRequest(

    @field:Schema(description = "액세스 토큰", required = true)
    val accessToken: String,
)
