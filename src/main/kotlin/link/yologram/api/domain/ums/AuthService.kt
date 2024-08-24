package link.yologram.api.domain.ums

import link.yologram.api.domain.ums.dto.LogoutResponse
import link.yologram.api.domain.ums.dto.ValidateAccessTokenResponse
import link.yologram.api.domain.ums.dto.AccessTokenClaim
import link.yologram.api.domain.ums.dto.LoginResponse
import link.yologram.api.domain.ums.exception.AuthException
import link.yologram.api.domain.ums.exception.UserNotFoundException
import link.yologram.api.domain.ums.exception.WrongPasswordException
import link.yologram.api.infrastructure.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val accessTokenService: AccessTokenService,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    @Throws(UserNotFoundException::class, WrongPasswordException::class)
    fun login(email: String, password: String) : LoginResponse {
        val user = userRepository.findByEmail(email)

        /** User doesn't exists */
        if (user.isEmpty) throw UserNotFoundException("User not found")

        /** Wrong password */
        if (!passwordEncoder.matches(password, user.get().password)) throw WrongPasswordException("Wrong password.")

        /** Access token has already been issued */
        if (!user.get().accessToken.isNullOrBlank()) {
            return try {
                accessTokenService.parseAsAccessTokenClaim(user.get().accessToken!!)
                LoginResponse(uid = user.get().id, accessToken = user.get().accessToken!!)
            } catch (e: AuthException) {
                val accessToken = accessTokenService.generate(AccessTokenClaim(uid = user.get().id))
                user.get().accessToken = accessToken
                LoginResponse(uid = user.get().id, accessToken = accessToken)
            }
        }

        /** Issue new access token */
        val accessToken = accessTokenService.generate(AccessTokenClaim(uid = user.get().id))
        user.get().accessToken = accessToken
        return LoginResponse(uid = user.get().id, accessToken = accessToken)
    }

    @Transactional(readOnly = true)
    @Throws(UserNotFoundException::class)
    fun validateToken(uid: Long, accessToken: String): ValidateAccessTokenResponse {
        val user = userRepository.findById(uid).orElseThrow { UserNotFoundException("User not found") }
        if (uid != user.id) throw AuthException("Invalid Access Token")
        accessTokenService.parseAsAccessTokenClaim(accessToken)
        return ValidateAccessTokenResponse(uid = uid, accessToken = accessToken)
    }

    @Transactional
    @Throws
    fun logout(uid: Long, accessToken: String): LogoutResponse {
        val user = userRepository.findById(uid).orElseThrow { UserNotFoundException("User not found") }
        if (uid != user.id) throw AuthException("Invalid Access Token")
        accessTokenService.parseAsAccessTokenClaim(accessToken)
        user.accessToken = null
        return LogoutResponse(uid = uid)
    }
}