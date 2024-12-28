package link.yologram.api.domain.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import link.yologram.api.config.JwtConfig
import link.yologram.api.decodeBase64
import link.yologram.api.deserialize
import link.yologram.api.domain.auth.dto.JwtClaim
import link.yologram.api.domain.auth.exception.AuthException
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil(
    private val jwtConfig: JwtConfig
) {
    private val jwtVerifier =
        JWT.require(Algorithm.HMAC256(jwtConfig.secret))
            .withIssuer(jwtConfig.issuer)
            .withAudience(jwtConfig.audience)
            .build()

    fun createToken(jwtClaim: JwtClaim): String = try {
        val expireDate = Date(System.currentTimeMillis() + (jwtConfig.expireInSeconds * 1_000))
        val jwtBuilder = JWT.create()
            .withIssuer(jwtConfig.issuer)
            .withAudience(jwtConfig.audience)
            .withExpiresAt(expireDate)

        jwtBuilder
            .withClaim("uid", jwtClaim.uid)
            .sign(Algorithm.HMAC256(jwtConfig.secret))
    } catch (e: JWTCreationException) {
        throw AuthException("Fail to create jwt token. ${e.message}")
    }

    fun getTokenClaim(token: String): JwtClaim = try {
        val json = validateToken(token)
        json deserialize JwtClaim::class
    } catch (e: MismatchedInputException) {
        throw AuthException("Fail to parse jwt token")
    }

    fun validateToken(token: String): String = try {
        jwtVerifier.verify(token).payload.decodeBase64()
    } catch (e: TokenExpiredException) {
        throw AuthException("Expired jwt token")
    } catch (e: JWTVerificationException) {
        throw AuthException("Invalid jwt token")
    }
}