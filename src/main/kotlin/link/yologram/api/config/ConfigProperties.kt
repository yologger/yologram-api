package link.yologram.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "yologram.auth.jwt")
class JwtConfig {
    var issuer: String = "yologram.co.kr"
    var audience: String = "yologram.client"
    lateinit var secret: String
}

@Component
@ConfigurationProperties(prefix = "yologram.auth.access-token")
class AccessTokenConfig (
    val jwtConfig: JwtConfig
) {
    var expireInSeconds: Long = 5

    var secret = ""
        get() = jwtConfig.secret
}