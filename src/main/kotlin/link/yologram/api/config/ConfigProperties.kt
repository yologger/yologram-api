package link.yologram.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "yologram.auth.jwt")
class JwtConfig {
    var issuer: String = "yologram.link"
    var audience: String = "yologram.client"
    var expireInSeconds: Long = 0
    lateinit var secret: String
}