package link.yologram.api.domain.ums.resource

import any
import link.yologram.api.common.WebMvcTestSupport
import link.yologram.api.domain.ums.exception.UserDuplicateException
import link.yologram.api.domain.ums.model.*
import link.yologram.api.domain.ums.service.UserService
import link.yologram.api.domain.ums.util.JwtUtil
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.utlis.isCreated
import org.mockito.BDDMockito.given
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@WebMvcTestSupport(
    controllers = [UserResource::class]
)
class UserResourceTest(
    @Autowired val jwtUtil: JwtUtil,
    @Autowired var client: WebTestClient
) {

    @MockBean
    lateinit var userService: UserService

    @Nested
    @DisplayName("회원 가입")
    inner class JoinTest {

        val request = mapOf<String, Any>(
            "email" to "yologger1013@gmail.com",
            "name" to "yologger",
            "nickname" to "yologger",
            "password" to "1234A2dss@!"
        )

        @Test
        @DisplayName("회원 가입에 성공한 경우, 201을 반환한다")
        fun `회원 가입에 성공한 경우, 201을 반환한다`() {

            // Given
            given(
                userService.join(any())
            ).willReturn(APIEnvelop(data = JoinResponse(1)))

            // When & Then
            client.post()
                .uri(URI_USER_JOIN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .isCreated()
        }

        @Test
        @DisplayName("이미 유저가 존재하는 경우, 회원가입에 실패하고 409를 반환한다")
        fun `이미 유저가 존재하는 경우, 회원가입에 실패하고 409를 반환한다`() {

            val email = "yologger1013@gmail.com"
            val errorMessage = "user '${email}' already exists."

            // Given
            given(
                userService.join(any())
            ).willThrow(UserDuplicateException(errorMessage))

            // When & Then
            client.post()
                .uri(URI_USER_JOIN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo(errorMessage)
                .jsonPath("$.errorCode").isEqualTo("USER_DUPLICATE")

        }
    }

    @Nested
    @DisplayName("유저 정보 조회")
    inner class UserGetTest {
        @Test
        @DisplayName("계정이 존재할 때, 유저 정보를 반환하고 200을 응답한다")
        fun `계정이 존재할 때, 유저 정보를 반환하고 200을 응답한다`() {
            val email = "yologger1013@gmail.com"
            val accessToken = jwtUtil.createToken(JwtClaim(uid = UID))

            // Given
            val response = APIEnvelop(
                data = UserData(
                    uid = UID,
                    email = email,
                    name = "yologger",
                    nickname = "yologger",
                    joinedDate = LocalDateTime.now()
                )
            )
            given(userService.getUser(UID)).willReturn(response)

            // When & Then
            client.get()
                .uri("$URI_USER_GET/$UID")
                .header(AuthData.USER_KEY, "$accessToken")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data.uid").isEqualTo(UID)
                .jsonPath("$.data.email").isEqualTo(email)
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    inner class WithdrawTest {

        @Test
        @DisplayName("회원 탈퇴가 성공하면 200을 반환한다")
        fun `회원 탈퇴 성공`() {
            val email = "yologger1013@gmail.com"
            val accessToken = jwtUtil.createToken(JwtClaim(uid = UID))

            val response = APIEnvelop(
                data = WithdrawResponse(
                    uid = UID,
                    deletedAt = LocalDateTime.now(),
                    deletedBoardsCount = 3
                )
            )

            // Given
            given(userService.withdraw(UID)).willReturn(response)

            // When & Then
            client.delete()
                .uri(URI_USER_WITHDRAW)
                .header(AuthData.USER_KEY, "$accessToken")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data.uid").isEqualTo(UID)
        }
    }

    companion object {
        const val URI_USER_JOIN = "/api/ums/v1/user/join"
        const val URI_USER_GET = "/api/ums/v1/user"
        const val URI_USER_WITHDRAW = "/api/ums/v1/user/withdraw"
        const val UID: Long = 235
    }
}