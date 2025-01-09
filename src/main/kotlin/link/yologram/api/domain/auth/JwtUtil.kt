package link.yologram.api.domain.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import link.yologram.api.config.JwtConfig
import link.yologram.api.global.decodeBase64
import link.yologram.api.global.deserialize
import link.yologram.api.domain.auth.dto.JwtClaim
import link.yologram.api.domain.auth.exception.ExpiredTokenException
import link.yologram.api.domain.auth.exception.TokenCreationFailException
import link.yologram.api.domain.auth.exception.InvalidTokenException
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
        throw TokenCreationFailException("Fail to create jwt token. ${e.message}")
    }

    fun getTokenClaim(token: String): JwtClaim = try {
        val json = validateToken(token)
        json deserialize JwtClaim::class
    } catch (e: MismatchedInputException) {
        throw InvalidTokenException("Fail to parse jwt token")
    }

    fun validateToken(token: String): String = try {
        jwtVerifier.verify(token).payload.decodeBase64()
    } catch (e: TokenExpiredException) {
        throw ExpiredTokenException("Expired jwt token")
    } catch (e: JWTVerificationException) {
        throw InvalidTokenException("Invalid jwt token")
    }
}