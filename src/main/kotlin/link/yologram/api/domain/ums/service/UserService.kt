package link.yologram.api.domain.ums.service

import link.yologram.api.domain.bms.enum.BoardStatus
import link.yologram.api.domain.ums.model.JoinRequest
import link.yologram.api.domain.ums.model.JoinResponse
import link.yologram.api.domain.ums.model.UserData
import link.yologram.api.domain.ums.model.WithdrawResponse
import link.yologram.api.domain.ums.exception.UserDuplicateException
import link.yologram.api.domain.ums.exception.UserAlreadyDeletedException
import link.yologram.api.domain.ums.exception.UserNotFoundException
import link.yologram.api.domain.ums.entity.User
import link.yologram.api.domain.bms.repository.board.BoardRepository
import link.yologram.api.domain.ums.enum.UserStatus
import link.yologram.api.domain.ums.repository.UserRepository
import link.yologram.api.global.model.APIEnvelop
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
    @Throws(UserDuplicateException::class)
    fun join(request: JoinRequest): APIEnvelop<JoinResponse> {

        userRepository.findByEmail(request.email).ifPresent { throw UserDuplicateException("user '${request.email}' already exists.") }

        val saved = userRepository.save(
            User(
                email = request.email,
                name = request.name,
                nickname = request.nickname,
                password = passwordEncoder.encode(request.password)
            )
        )
        return APIEnvelop(data = JoinResponse(uid = saved.id))
    }

    @Transactional(readOnly = true, rollbackFor = [Exception::class])
    @Throws(UserNotFoundException::class)
    fun getUser(uid: Long): APIEnvelop<UserData> {
        val user = UserData.fromEntity(userRepository.findByIdOrNull(uid) ?: throw UserNotFoundException("User uid: $uid not found."))
        return APIEnvelop(data = user)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun withdraw(uid: Long): APIEnvelop<WithdrawResponse> {
        val user = userRepository.findById(uid).orElseThrow { UserNotFoundException("User not exists") }
        if (user.status == UserStatus.DELETED) throw UserAlreadyDeletedException("User already deleted")
        val deletedBoardCount = boardRepository.updateBoardStatusByUid(uid = user.id, status = BoardStatus.DELETED)
        user.status = UserStatus.DELETED
        user.deletedDate = LocalDateTime.now()
        return APIEnvelop(data = WithdrawResponse(uid = uid, deletedAt = user.deletedDate, deletedBoardsCount = deletedBoardCount))
    }
}