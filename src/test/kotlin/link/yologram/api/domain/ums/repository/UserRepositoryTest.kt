package link.yologram.api.domain.ums.repository

import link.yologram.api.common.AbstractRepositoryDataJpaTest
import link.yologram.api.domain.ums.entity.User
import link.yologram.api.domain.ums.enum.UserStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("UserRepository 테스트")
class UserRepositoryTest(
    @Autowired val userRepository: UserRepository,
) : AbstractRepositoryDataJpaTest() {

    @Test
    @DisplayName("User 단건 추가")
    fun `User 단건 추가`() {

        val dummyUser = User(
            email = "jane.doe@example.com",
            name = "Jane Doe",
            nickname = "janey",
            password = "securePassword123!",
            accessToken = "dummyAccessToken123",
            status = UserStatus.ACTIVE,
        )
        val saved = userRepository.save(dummyUser)
        val fetched = assertDoesNotThrow {
            userRepository.findByEmail(dummyUser.email)
        }
        assertThat(fetched.get().name).isEqualTo(saved.name)
    }

    @Test
    @DisplayName("User 단건 조회")
    fun `User 단건 조회`() {
        val uid = 1L // email: ronaldo@gmail.com
        val fetched = userRepository.findById(uid)
        assertThat(fetched.get().email).isEqualTo("ronaldo@gmail.com")
    }

}