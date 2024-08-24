package link.yologram.api.domain.ums

import link.yologram.api.domain.ums.dto.JoinRequest
import link.yologram.api.domain.ums.dto.JoinResponse
import link.yologram.api.domain.ums.dto.UserData
import link.yologram.api.domain.ums.exception.DuplicateUserException
import link.yologram.api.domain.ums.exception.UserNotFoundException
import link.yologram.api.infrastructure.entity.User
import link.yologram.api.infrastructure.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    @Throws(DuplicateUserException::class)
    fun join(request: JoinRequest): JoinResponse {

        if (userRepository.findByEmail(request.email).isPresent) {
            logger.info("User '${request.email}' already exists.")
            throw DuplicateUserException("User '${request.email}' already exists.")
        }

        val saved = userRepository.save(
            User(
                email = request.email,
                name = request.name,
                nickname = request.nickname,
                password = passwordEncoder.encode(request.password)
            )
        )
        return JoinResponse(uid = saved.id)
    }

    @Transactional
    @Throws(UserNotFoundException::class)
    fun getUser(uid: Long): UserData {
        return UserData.fromEntity(userRepository.findByIdOrNull(uid) ?: throw UserNotFoundException("User uid: $uid not found."))
    }
}