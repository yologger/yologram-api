package link.yologram.api.domain.ums

import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import link.yologram.api.config.AccessTokenConfig
import link.yologram.api.decodeBase64
import link.yologram.api.deserialize
import link.yologram.api.domain.ums.dto.AccessTokenClaim
import link.yologram.api.domain.ums.exception.AuthException
import org.springframework.stereotype.Service

@Service
class AccessTokenService (
    private val accessTokenConfig: AccessTokenConfig,
    private val jwtService: JwtService
) {
    fun generate(claim: AccessTokenClaim): String = try {
        jwtService.createBuilder(accessTokenConfig.expireInSeconds)
            .withClaim("uid", claim.uid)
            .sign(Algorithm.HMAC256(accessTokenConfig.secret))
    } catch (e: JWTCreationException) {
        throw AuthException("Fail to create access token. ${e.message}")
    }

    fun parseAsAccessTokenClaim(accessToken: String): AccessTokenClaim {
        try {
            val json = verifyAndParse(accessToken)
            return json deserialize AccessTokenClaim::class
        } catch (e: TokenExpiredException) {
            throw AuthException("Expired access token")
        } catch (e: JWTVerificationException) {
            throw AuthException("Invalid access token")
        }
    }

    private fun verifyAndParse(jwtToken: String) = try {
        jwtService.verifier().verify(jwtToken).payload.decodeBase64()
    } catch (e: MismatchedInputException) {
        throw AuthException("Fail to verify and parse access token")
    }
}