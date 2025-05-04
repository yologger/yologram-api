package link.yologram.api.domain.ums.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

@Schema(description = "로그인을 위한 request 모델")
data class LoginRequest(

    @Schema(description = "유저 이메일", required = true, example = "cr7@gmail.com")
    @field:Email(message = "Invalid email format")
    val email: String,

    @Schema(description = "유저 비밀번호", required = true, example = "1234ASdf!@#")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
        message = "'password' must contain at least one uppercase letter, lowercase letter, and special character, and can have a minimum of 8 characters and a maximum of 20 characters."
    )
    val password: String
)
