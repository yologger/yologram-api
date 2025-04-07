package link.yologram.api.infrastructure.repository


import link.yologram.api.domain.ums.entity.User
import link.yologram.api.domain.ums.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

@Disabled
class UserRepositoryTest(
    @Autowired private val userRepository: UserRepository
): AbstractDataJpaTest() {
    @Test
    @DisplayName("User 단건 추가")
    fun addUser() {
        val email = "yologger1013@gmail.com"

        userRepository.save(
            User(
                email = email,
                name = "yologger1013",
                nickname = "yologger",
                password = "12341234"
            )
        )

        val user = userRepository.findByEmail(email)
        assertThat(user.get().email).isEqualTo(email)
    }

    @Test
    @DisplayName("User 벌크 조회")
    @Sql(scripts = ["/sql/repository/insert_bulk_users.sql"])
    fun findUsers() {
        val users = userRepository.findAll()
        println(users)
        assertThat(users.size).isGreaterThanOrEqualTo(5)
    }
}