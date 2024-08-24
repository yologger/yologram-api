package link.yologram.api.domain.ums.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*

data class ValidateAccessTokenRequest(
    @field:Positive
    val uid: Long,

    @field:NotBlank(message = "'access_token' must not be empty.")
    @JsonProperty("access_token")
    val accessToken: String
)
