package link.yologram.api.domain.auth.service

import link.yologram.api.domain.auth.JwtUtil
import link.yologram.api.domain.auth.dto.JwtClaim
import link.yologram.api.domain.ums.dto.LogoutResponse
import link.yologram.api.domain.ums.dto.LoginResponse
import link.yologram.api.domain.auth.exception.AuthException
import link.yologram.api.domain.auth.exception.InvalidTokenOwnerException
import link.yologram.api.domain.auth.exception.UserNotFoundException
import link.yologram.api.domain.auth.exception.WrongPasswordException
import link.yologram.api.domain.ums.dto.ValidateAccessTokenResponse
import link.yologram.api.domain.ums.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    @Transactional(rollbackFor = [Exception::class])
    @Throws(UserNotFoundException::class, WrongPasswordException::class)
    fun login(email: String, password: String) : LoginResponse {
        val user = userRepository.findByEmail(email).orElseThrow { UserNotFoundException("User not found") }

        /** Wrong password */
        if (!passwordEncoder.matches(password, user.password)) throw WrongPasswordException("Wrong password.")

        /** Access token has already been issued */
        if (!user.accessToken.isNullOrBlank()) {
            return try {
                jwtUtil.validateToken(user.accessToken!!)
                LoginResponse(uid = user.id, accessToken = user.accessToken!!, email = user.email, name = user.name, nickname = user.nickname)
            } catch (e: AuthException) {
                val accessToken = jwtUtil.createToken(JwtClaim(uid = user.id))
                user.accessToken = accessToken
                LoginResponse(uid = user.id, accessToken = accessToken, email = user.email, name = user.name, nickname = user.nickname)
            }
        }

        /** Issue new access token */
        val accessToken = jwtUtil.createToken(JwtClaim(uid = user.id))
        user.accessToken = accessToken
        return LoginResponse(uid = user.id, accessToken = accessToken, email = user.email, name = user.name, nickname = user.nickname)
    }

    @Transactional(rollbackFor = [Exception::class])
    @Throws
    fun logout(uid: Long, accessToken: String): LogoutResponse {
        val user = userRepository.findById(uid).orElseThrow { UserNotFoundException("User not found") }
        if (uid != user.id) throw InvalidTokenOwnerException("Invalid token owner")
        jwtUtil.validateToken(accessToken)
        user.accessToken = null
        return LogoutResponse(uid = uid)
    }

    @Transactional(rollbackFor = [Exception::class])
    @Throws
    fun validateToken(accessToken: String, uid: Long): ValidateAccessTokenResponse {
        val user = userRepository.findById(uid).orElseThrow { UserNotFoundException("User not found") }
        if (uid != user.id) throw InvalidTokenOwnerException("Invalid token owner")
        return ValidateAccessTokenResponse(accessToken = accessToken, uid = uid, email = user.email, name = user.name, nickname = user.nickname)
    }
}