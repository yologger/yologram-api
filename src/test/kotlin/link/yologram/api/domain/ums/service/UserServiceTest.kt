package link.yologram.api.domain.ums.service

import any
import link.yologram.api.domain.ums.dto.JoinRequest
import link.yologram.api.domain.ums.exception.DuplicateUserException
import link.yologram.api.infrastructure.entity.User
import link.yologram.api.infrastructure.repository.BoardRepository
import link.yologram.api.infrastructure.repository.UserRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito.mock
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserServiceTest {
    private val userRepository: UserRepository = mock()
    private val boardRepository: BoardRepository = mock()
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    private val service = UserService(userRepository = userRepository, passwordEncoder = passwordEncoder, boardRepository = boardRepository)

    @Nested
    @DisplayName("회원가입")
    inner class JoinTest {

        @Test
        fun `회원가입에 성공했을 때`() {

            // Given
            val request = JoinRequest(
                email = "yologger1013@gmail.com",
                name = "yologger",
                nickname = "yologger",
                password = "1234"
            )

            BDDMockito.given(
                userRepository.findByEmail(any())
            ).willReturn(
                Optional.empty()
            )

            BDDMockito.given(
                userRepository.save(any())
            ).willReturn(
                User(
                    id = 1,
                    email = "yologger1013@gmail.com",
                    name = "yologger",
                    nickname = "yologger",
                    password = "1234",
                    accessToken = null
                )
            )

            // When, Then
            val response = service.join(request)
            assertThat(response.uid).isNotNull
        }

        @Test
        fun `이미 계정이 존재할 때`() {

            // Given
            val request = JoinRequest(
                email = "yologger1013@gmail.com",
                name = "yologger",
                nickname = "yologger",
                password = "1234"
            )

            BDDMockito.given(
                userRepository.findByEmail(any())
            ).willReturn(
                Optional.of(
                    User(
                        email = "yologger1013@gmail.com",
                        name = "yologger",
                        nickname = "yologger",
                        password = "1234")
                )
            )

            // When, Then
            Assertions.assertThatThrownBy {
                service.join(request)
            }.isExactlyInstanceOf(DuplicateUserException::class.java)
        }
    }
}