package link.yologram.api.domain.ums

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import link.yologram.api.config.JwtConfig
import org.springframework.stereotype.Component
import java.util.*


@Component
class JwtService (
    private val config: JwtConfig
) {
    private val jwtVerifier =
        JWT.require(Algorithm.HMAC256(config.secret))
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .build()

    fun verifier(): JWTVerifier = jwtVerifier

    fun createBuilder(expireInSeconds: Long): JWTCreator.Builder {
        val expireDate = Date(System.currentTimeMillis() + (expireInSeconds * 1_000))
        return JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withExpiresAt(expireDate)
    }
}