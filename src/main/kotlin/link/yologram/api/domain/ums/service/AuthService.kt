package link.yologram.api.domain.ums.service

import link.yologram.api.domain.ums.util.JwtUtil
import link.yologram.api.domain.ums.model.JwtClaim
import link.yologram.api.domain.ums.model.LogoutResponse
import link.yologram.api.domain.ums.model.LoginResponse
import link.yologram.api.domain.ums.exception.UserNotFoundException
import link.yologram.api.domain.ums.exception.AuthWrongPasswordException
import link.yologram.api.domain.ums.model.ValidateAccessTokenResponse
import link.yologram.api.domain.ums.exception.AuthInvalidTokenOwnerException
import link.yologram.api.domain.ums.exception.UmsException
import link.yologram.api.domain.ums.repository.UserRepository
import link.yologram.api.global.model.APIEnvelop
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
    @Throws(UserNotFoundException::class, AuthWrongPasswordException::class)
    fun login(email: String, password: String) : APIEnvelop<LoginResponse> {
        val user = userRepository.findByEmail(email).orElseThrow { UserNotFoundException("User not found") }

        /** Wrong password */
        if (!passwordEncoder.matches(password, user.password)) throw AuthWrongPasswordException("Wrong password.")

        /** Access token has already been issued */
        if (!user.accessToken.isNullOrBlank()) {
            return try {
                jwtUtil.validateToken(user.accessToken!!)
                APIEnvelop(data = LoginResponse(uid = user.id, accessToken = user.accessToken!!, email = user.email, name = user.name, nickname = user.nickname))
            } catch (e: UmsException) {
                val accessToken = jwtUtil.createToken(JwtClaim(uid = user.id))
                user.accessToken = accessToken
                APIEnvelop(data = LoginResponse(uid = user.id, accessToken = accessToken, email = user.email, name = user.name, nickname = user.nickname))
            }
        }

        /** Issue new access token */
        val accessToken = jwtUtil.createToken(JwtClaim(uid = user.id))
        user.accessToken = accessToken
        return APIEnvelop(data = LoginResponse(uid = user.id, accessToken = accessToken, email = user.email, name = user.name, nickname = user.nickname))
    }

    @Transactional(rollbackFor = [Exception::class])
    @Throws
    fun validateToken(accessToken: String, uid: Long): APIEnvelop<ValidateAccessTokenResponse> {
        val user = userRepository.findById(uid).orElseThrow { UserNotFoundException("User not found") }
        if (uid != user.id) throw AuthInvalidTokenOwnerException("Invalid token owner")
        return APIEnvelop(data = ValidateAccessTokenResponse(accessToken = accessToken, uid = uid, email = user.email, name = user.name, nickname = user.nickname))
    }

    @Transactional(rollbackFor = [Exception::class])
    @Throws
    fun logout(uid: Long, accessToken: String): APIEnvelop<LogoutResponse> {
        val user = userRepository.findById(uid).orElseThrow { UserNotFoundException("User not found") }
        if (uid != user.id) throw AuthInvalidTokenOwnerException("Invalid token owner")
        jwtUtil.validateToken(accessToken)
        user.accessToken = null
        return APIEnvelop(data = LogoutResponse(uid = uid))
    }
}