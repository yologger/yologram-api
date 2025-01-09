package link.yologram.api.domain.ums.service

import link.yologram.api.domain.ums.dto.JoinRequest
import link.yologram.api.domain.ums.dto.JoinResponse
import link.yologram.api.domain.ums.dto.UserData
import link.yologram.api.domain.ums.dto.WithdrawResponse
import link.yologram.api.domain.ums.exception.DuplicateUserException
import link.yologram.api.domain.ums.exception.UserAlreadyDeletedException
import link.yologram.api.domain.ums.exception.UserNotFoundException
import link.yologram.api.infrastructure.entity.User
import link.yologram.api.infrastructure.enum.BoardStatus
import link.yologram.api.infrastructure.enum.UserStatus
import link.yologram.api.infrastructure.repository.BoardRepository
import link.yologram.api.infrastructure.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val boardRepository: BoardRepository
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional(rollbackFor = [Exception::class])
    @Throws(DuplicateUserException::class)
    fun join(request: JoinRequest): JoinResponse {

        userRepository.findByEmail(request.email).ifPresent { throw DuplicateUserException("user '${request.email}' already exists.") }

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

    @Transactional(readOnly = true, rollbackFor = [Exception::class])
    @Throws(UserNotFoundException::class)
    fun getUser(uid: Long): UserData {
        return UserData.fromEntity(userRepository.findByIdOrNull(uid) ?: throw UserNotFoundException("User uid: $uid not found."))
    }

    @Transactional(rollbackFor = [Exception::class])
    fun withdraw(uid: Long): WithdrawResponse {
        val user = userRepository.findById(uid).orElseThrow { UserNotFoundException("User not exists") }
        if (user.status == UserStatus.DELETED) throw UserAlreadyDeletedException("User already deleted")
        val deletedBoardCount = boardRepository.updateBoardStatusByUid(uid = user.id, status = BoardStatus.DELETED)
        user.status = UserStatus.DELETED
        user.deletedDate = LocalDateTime.now()
        return WithdrawResponse(uid = uid, deletedAt = user.deletedDate, deletedBoardsCount = deletedBoardCount)
    }
}