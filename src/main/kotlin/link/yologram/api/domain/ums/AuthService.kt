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
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    @Transactional
    @Throws(UserNotFoundException::class, WrongPasswordException::class)
    fun login(email: String, password: String) : LoginResponse {
        val user = userRepository.findByEmail(email).orElseThrow { UserNotFoundException("User not found") }

        /** Wrong password */
        if (!passwordEncoder.matches(password, user.password)) throw WrongPasswordException("Wrong password.")

        /** Access token has already been issued */
        if (!user.accessToken.isNullOrBlank()) {
            return try {
                jwtUtil.validateToken(user.accessToken!!)
                LoginResponse(uid = user.id, accessToken = user.accessToken!!)
            } catch (e: AuthException) {
                val accessToken = jwtUtil.createToken(JwtClaim(uid = user.id))
                user.accessToken = accessToken
                LoginResponse(uid = user.id, accessToken = accessToken)
            }
        }

        /** Issue new access token */
        val accessToken = jwtUtil.createToken(JwtClaim(uid = user.id))
        user.accessToken = accessToken
        return LoginResponse(uid = user.id, accessToken = accessToken)
    }

    @Transactional(readOnly = true)
    @Throws(UserNotFoundException::class)
    fun validateToken(uid: Long, accessToken: String): ValidateAccessTokenResponse {
        val user = userRepository.findById(uid).orElseThrow { UserNotFoundException("User not found") }
        if (uid != user.id) throw AuthException("Invalid jwt token")
        jwtUtil.validateToken(accessToken)
        return ValidateAccessTokenResponse(uid = uid, accessToken = accessToken)
    }

    @Transactional
    @Throws
    fun logout(uid: Long, accessToken: String): LogoutResponse {
        val user = userRepository.findById(uid).orElseThrow { UserNotFoundException("User not found") }
        if (uid != user.id) throw AuthException("Invalid jwt token")
        jwtUtil.validateToken(accessToken)
        user.accessToken = null
        return LogoutResponse(uid = uid)
    }
}