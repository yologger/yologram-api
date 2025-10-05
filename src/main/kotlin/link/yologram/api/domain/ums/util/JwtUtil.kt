package link.yologram.api.domain.ums.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import link.yologram.api.config.JwtConfig
import link.yologram.api.domain.bms.exception.UserNotFoundException
import link.yologram.api.global.extension.decodeBase64
import link.yologram.api.global.extension.deserialize
import link.yologram.api.domain.ums.model.JwtClaim
import link.yologram.api.domain.ums.exception.AuthTokenExpiredException
import link.yologram.api.domain.ums.exception.AuthTokenCreationFailureException
import link.yologram.api.domain.ums.exception.AuthTokenInvalidException
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
        throw AuthTokenCreationFailureException("Fail to create token. ${e.message}")
    }

    fun getTokenClaim(token: String): JwtClaim = try {
        val json = validateToken(token)
        json deserialize JwtClaim::class
    } catch (e: MismatchedInputException) {
        throw AuthTokenInvalidException("Fail to parse token")
    }


    fun validateToken(token: String): String = try {
        jwtVerifier.verify(token).payload.decodeBase64()
    } catch (e: TokenExpiredException) {
        throw AuthTokenExpiredException("Expired token")
    } catch (e: JWTVerificationException) {
        throw AuthTokenInvalidException("Invalid token")
    }
}