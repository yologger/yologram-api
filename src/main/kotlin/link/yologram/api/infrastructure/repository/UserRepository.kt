package link.yologram.api.infrastructure.repository

import link.yologram.api.infrastructure.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

interface UserRepository: JpaRepository<User, Long> {

    @Transactional(readOnly = true)
    fun findByEmail(email: String): Optional<User>

    @Transactional(readOnly = true)
    fun findUserByEmail(email: String): User

    @Transactional(readOnly = true)
    override fun existsById(id: Long): Boolean
}