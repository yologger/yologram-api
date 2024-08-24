package link.yologram.api.domain.ums.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class JoinRequest(

    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "'name' must not be empty.")
    @field:Size(min = 4, max = 20, message = "'name' length must be between 4 and 20.")
    val name: String,

    @field:NotBlank(message = "'nickname' must not be empty.")
    @field:Size(min = 4, max = 20, message = "'nickname' length must be between 4 and 20.")
    val nickname: String,

    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
        message = "'password' must contain at least one uppercase letter, lowercase letter, and special character, and can have a minimum of 8 characters and a maximum of 20 characters."
    )
    val password: String
)
