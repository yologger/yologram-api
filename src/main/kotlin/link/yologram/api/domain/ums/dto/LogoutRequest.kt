package link.yologram.api.domain.ums.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*

data class LogoutRequest(
    @field:Positive
    val uid: Long
)
