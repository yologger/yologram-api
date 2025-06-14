package link.yologram.api.domain.ums.service

import any
import link.yologram.api.domain.ums.model.JoinRequest
import link.yologram.api.domain.ums.exception.UserDuplicateException
import link.yologram.api.domain.ums.entity.User
import link.yologram.api.domain.bms.repository.BoardRepository
import link.yologram.api.domain.ums.enum.UserStatus
import link.yologram.api.domain.ums.exception.UserAlreadyDeletedException
import link.yologram.api.domain.ums.exception.UserNotFoundException
import link.yologram.api.domain.ums.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito.mock
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
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
        @DisplayName("회원가입에 성공했을 때")
        fun `회원가입에 성공했을 때`() {

            // Given
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

            val request = JoinRequest(
                email = "yologger1013@gmail.com",
                name = "yologger",
                nickname = "yologger",
                password = "1234"
            )

            // When, Then
            val response = service.join(request)
            assertThat(response.data.uid).isNotNull
        }

        @Test
        @DisplayName("이미 계정이 존재할 때")
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
                        password = "1234"
                    )
                )
            )

            // When, Then
            assertThatThrownBy {
                service.join(request)
            }.isExactlyInstanceOf(UserDuplicateException::class.java)
        }
    }

    @Nested
    @DisplayName("회원 조회")
    inner class GetUserTest {
        @Test
        @DisplayName("계정이 존재할 때")
        fun `계정이 존재할 때`() {

            // Given
            val uid: Long = 1
            val email = "yologer1013@gmail.com"

            BDDMockito.given(
                userRepository.findById(any())
            ).willReturn(
                Optional.of(
                    User(
                        id = uid,
                        email = email,
                        name = "yologger",
                        nickname = "yologger",
                        password = "1234",
                    ).apply {
                        joinedDate = LocalDateTime.now()
                    }
                )
            )

            // When
            val result = service.getUser(uid = uid)

            // Then
            assertThat(result.data.email).isEqualTo(email)
        }

        @Test
        @DisplayName("계정이 존재하지 않을 때")
        fun `계정이 존재하지 않을 때`() {

            // Given
            val uid: Long = 1
            val email = "yologer1013@gmail.com"

            BDDMockito.given(
                userRepository.findById(any())
            ).willReturn(
                Optional.empty()
            )

            // When
            assertThatThrownBy {
                service.getUser(uid = uid)
            }.isExactlyInstanceOf(UserNotFoundException::class.java)
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    inner class WithdrawTest {
        @Test
        @DisplayName("계정이 존재하고 Status == ACTIVE 일 때")
        fun `계정이 존재하고 Status == ACTIVE 일 때`() {

            // Given
            val uid: Long = 1
            val email = "yologer1013@gmail.com"

            BDDMockito.given(
                userRepository.findById(any())
            ).willReturn(
                Optional.of(
                    User(
                        id = uid,
                        email = email,
                        name = "yologger",
                        nickname = "yologger",
                        password = "1234",
                        status = UserStatus.ACTIVE
                    ).apply {
                        joinedDate = LocalDateTime.now()
                    }
                )
            )

            val deletedBoardsCount = 5
            BDDMockito.given(
                boardRepository.updateBoardStatusByUid(any(), any())
            ).willReturn(deletedBoardsCount)

            // When
            val result = service.withdraw(uid)
            assertThat(result.data.deletedBoardsCount).isEqualTo(deletedBoardsCount)
            assertThat(result.data.uid).isEqualTo(uid)
        }


        @Test
        @DisplayName("계정이 존재하고 Status == DELETE 일 때")
        fun `계정이 존재하고 Status == DELETE 일 때`() {

            // Given
            val uid: Long = 1
            val email = "yologer1013@gmail.com"

            BDDMockito.given(
                userRepository.findById(any())
            ).willReturn(
                Optional.of(
                    User(
                        id = uid,
                        email = email,
                        name = "yologger",
                        nickname = "yologger",
                        password = "1234",
                        status = UserStatus.DELETED
                    ).apply {
                        joinedDate = LocalDateTime.now()
                    }
                )
            )

            val deletedBoardsCount = 5
            BDDMockito.given(
                boardRepository.updateBoardStatusByUid(any(), any())
            ).willReturn(deletedBoardsCount)

            // When & Then
            assertThatThrownBy {
                service.withdraw(uid)
            }.isExactlyInstanceOf(UserAlreadyDeletedException::class.java)
        }

        @Test
        @DisplayName("계정이 존재하지 않을 때")
        fun `계정이 존재하지 않을 때`() {

            // Given
            val uid: Long = 1

            BDDMockito.given(
                userRepository.findById(any())
            ).willReturn(
                Optional.empty()
            )

            val deletedBoardsCount = 5
            BDDMockito.given(
                boardRepository.updateBoardStatusByUid(any(), any())
            ).willReturn(deletedBoardsCount)

            // When & Then
            assertThatThrownBy {
                service.withdraw(uid)
            }.isExactlyInstanceOf(UserNotFoundException::class.java)
        }
    }
}