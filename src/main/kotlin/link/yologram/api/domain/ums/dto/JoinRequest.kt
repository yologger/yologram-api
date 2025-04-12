package link.yologram.api.domain.ums.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(
    description = "회원가입을 위한 request 모델"
)
data class JoinRequest(

    @Schema(description = "유저 이메일", required = true, example = "cr7@gmail.com")
    @field:Email(message = "Invalid email format")
    val email: String,

    @Schema(description = "유저 이름", required = true, example = "cristiano ronaldo")
    @field:NotBlank(message = "'name' must not be empty.")
    @field:Size(min = 4, max = 20, message = "'name' length must be between 4 and 20.")
    val name: String,

    @Schema(description = "유저 닉네임", required = true, example = "cr7")
    @field:NotBlank(message = "'nickname' must not be empty.")
    @field:Size(min = 3, max = 20, message = "'nickname' length must be between 4 and 20.")
    val nickname: String,

    @Schema(description = "유저 비밀번호", required = true, example = "1234ASdf!@#")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
        message = "'password' must contain at least one uppercase letter, lowercase letter, and special character, and can have a minimum of 8 characters and a maximum of 20 characters."
    )
    val password: String
)
