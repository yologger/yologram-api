package link.yologram.api.infra.api.datagokr

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "yologram.client.data-go-kr")
class DataGoKrProperties(
    @field:NotBlank
    val url: String,
    val connTimeout: Int,
    val readTimeout: Int,
    val writeTimeout: Int,
    val serviceKey: String,
)