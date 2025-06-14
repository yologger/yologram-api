package link.yologram.api.domain.ums.service

import any
import link.yologram.api.config.JwtConfig
import link.yologram.api.domain.ums.entity.User
import link.yologram.api.domain.ums.enum.UserStatus
import link.yologram.api.domain.ums.exception.AuthInvalidTokenOwnerException
import link.yologram.api.domain.ums.exception.AuthWrongPasswordException
import link.yologram.api.domain.ums.exception.UserNotFoundException
import link.yologram.api.domain.ums.model.JwtClaim
import link.yologram.api.domain.ums.repository.UserRepository
import link.yologram.api.domain.ums.util.JwtUtil
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.BDDMockito
import org.mockito.Mockito.mock
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.Test

class AuthServiceTest(
) {
    private val userRepository: UserRepository = mock()
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    private val jwtUtil = JwtUtil(
        JwtConfig().apply {
            issuer = "test-issuer"
            audience = "test-audience"
            expireInSeconds = 3600
            secret = "test-secret"
        }
    )
    private val authService = AuthService(userRepository, passwordEncoder, jwtUtil)


    @Nested
    @DisplayName("로그인")
    inner class LoginTest {

        @Test
        @DisplayName("로그인 성공 테스트 - 첫 access token 발급")
        fun `로그인 성공 테스트 - 첫 access token 발급`() {
            // Given
            val email = "tester@example.com"
            val password = "tester_password_1234@"

            BDDMockito.given(userRepository.findByEmail(any())).willReturn(
                Optional.of(
                    User(
                        id = 1,
                        email = email,
                        name = "tester",
                        nickname = "tester",
                        password = passwordEncoder.encode(password),
                        accessToken = null
                    )
                )
            )
            val result = authService.login(email, password)

            assertDoesNotThrow {
                jwtUtil.validateToken(result.data.accessToken)
            }
        }

        @Test
        @DisplayName("로그인 성공 테스트 - 이미 access token 발급되었을 때")
        fun `로그인 성공 테스트 - 이미 access token 발급되었을 때`() {
            // Given
            val email = "tester@example.com"
            val password = "tester_password_1234@"
            val accessToken = jwtUtil.createToken(JwtClaim(uid = UID))

            BDDMockito.given(userRepository.findByEmail(any())).willReturn(
                Optional.of(
                    User(
                        id = 1,
                        email = email,
                        name = "tester",
                        nickname = "tester",
                        password = passwordEncoder.encode(password),
                        accessToken = accessToken
                    )
                )
            )
            val result = authService.login(email, password)

            assertDoesNotThrow {
                jwtUtil.validateToken(result.data.accessToken)
            }
        }

        @Test
        @DisplayName("로그인 실패 테스트 - 잘못된 비밀번호")
        fun `로그인 실패 테스트 - 잘못된 비밀번호`() {
            // Given
            val email = "tester@example.com"
            val password = "tester_password_1234@"

            BDDMockito.given(userRepository.findByEmail(any())).willReturn(
                Optional.of(
                    User(
                        id = 1,
                        email = email,
                        name = "tester",
                        nickname = "tester",
                        password = passwordEncoder.encode(password + "dummy"),
                        accessToken = null
                    )
                )
            )

            assertThatThrownBy {
                authService.login(email, password)
            }.isExactlyInstanceOf(AuthWrongPasswordException::class.java)

        }
    }

    @Nested
    @DisplayName("토큰 검증")
    inner class ValidateTokenTen {


        @Test
        @DisplayName("token이 유효할 때 테스트")
        fun `token이 유효할 때 테스트`() {
            // Given
            val email = "tester@example.com"
            val password = "tester_password_1234@"

            BDDMockito.given(
                userRepository.findById(any())
            ).willReturn(
                Optional.of(
                    User(
                        id = UID,
                        email = email,
                        name = "yologger",
                        nickname = "yologger",
                        password = password,
                        status = UserStatus.ACTIVE
                    ).apply {
                        joinedDate = LocalDateTime.now()
                    }
                )
            )

            val accessToken = jwtUtil.createToken(JwtClaim(uid = UID))

            // When
            assertDoesNotThrow {
                val result = authService.validateToken(uid = UID, accessToken = accessToken)
                assertThat(result.data.accessToken).isEqualTo(accessToken)
            }
        }


        @Test
        @DisplayName("token이 유효하나 소유주가 아닐 때")
        fun `token이 유효하나 소유주가 아닐 때`() {
            // Given
            val email = "tester@example.com"
            val password = "tester_password_1234@"

            BDDMockito.given(
                userRepository.findById(any())
            ).willReturn(
                Optional.of(
                    User(
                        id = UID,
                        email = email,
                        name = "yologger",
                        nickname = "yologger",
                        password = password,
                        status = UserStatus.ACTIVE
                    ).apply {
                        joinedDate = LocalDateTime.now()
                    }
                )
            )

            val accessToken = jwtUtil.createToken(JwtClaim(uid = UID))

            // When
            assertThatThrownBy {
                authService.validateToken(uid = UID + 1, accessToken = accessToken)
            }.isExactlyInstanceOf(AuthInvalidTokenOwnerException::class.java)
        }


        @Test
        @DisplayName("token 유저가 존재하지 않을 때")
        fun `token 유저가 존재하지 않을 때`() {
            // Given
            val email = "tester@example.com"
            val password = "tester_password_1234@"

            BDDMockito.given(
                userRepository.findById(any())
            ).willReturn(Optional.empty())

            val accessToken = jwtUtil.createToken(JwtClaim(uid = UID))

            // When
            assertThatThrownBy {
                authService.validateToken(uid = UID, accessToken = accessToken)
            }.isExactlyInstanceOf(UserNotFoundException::class.java)
        }
    }

    companion object {
        const val UID: Long = 1
    }
}
