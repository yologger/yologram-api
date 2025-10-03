package link.yologram.api.domain.ums.resource

import any
import link.yologram.api.common.WebMvcTestSupport
import link.yologram.api.domain.ums.exception.AuthWrongPasswordException
import link.yologram.api.domain.ums.exception.UserNotFoundException
import link.yologram.api.domain.ums.model.*
import link.yologram.api.domain.ums.service.AuthService
import link.yologram.api.domain.ums.util.JwtUtil
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.utlis.isOk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test

@WebMvcTestSupport(
    controllers = [AuthResource::class]
)
class AuthResourceTest(
    @Autowired val jwtUtil: JwtUtil,
    @Autowired var client: WebTestClient
) {

    @MockBean
    lateinit var authService: AuthService

    @Nested
    @DisplayName("로그인")
    inner class LoginTest {

        val request = mapOf<String, Any>(
            "email" to "yologger1013@gmail.com",
            "password" to "1234A2dss@!"
        )

        @Test
        @DisplayName("로그인에 성공한 경우, 201을 반환한다")
        fun `로그인에 성공한 경우, 201을 반환한다`() {

            // Given
            val uid: Long = 1
            val accessToken = jwtUtil.createToken(JwtClaim(uid = uid))
            val email = "tester1234@gmail.com"
            val nickname = "tester1234"
            val name = "tester1234"

            given(
                authService.login(any(), any())
            ).willReturn(
                APIEnvelop(
                    data = LoginResponse(
                        uid = uid,
                        accessToken = accessToken,
                        email = email,
                        nickname = nickname,
                        name = name,
                    )
                )
            )

            // When & Then
            client.post()
                .uri(URI_AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .isOk()
                .expectBody()
                .jsonPath("$.data.email").isEqualTo(email)
                .jsonPath("$.data.nickname").isEqualTo(nickname)
        }

        @Test
        @DisplayName("유저가 없는 경우, 404를 반환한다")
        fun `유저가 없는 경우 404를 반환한다`() {

            // Given
            val uid: Long = 1
            val accessToken = jwtUtil.createToken(JwtClaim(uid = uid))
            val email = "tester1234@gmail.com"
            val nickname = "tester1234"
            val name = "tester1234"

            given(
                authService.login(any(), any())
            ).willThrow(UserNotFoundException("User not found"))

            // When & Then
            client.post()
                .uri(URI_AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo("User not found")
                .jsonPath("$.errorCode").isEqualTo("USER_NOT_FOUND")
        }

        @Test
        @DisplayName("비밀번호가 틀린 경우, 401를 반환한다")
        fun `비밀번호가 틀린 경우, 401를 반환한다`() {

            // Given
            val uid: Long = 1
            val accessToken = jwtUtil.createToken(JwtClaim(uid = uid))
            val email = "tester1234@gmail.com"
            val nickname = "tester1234"
            val name = "tester1234"

            given(
                authService.login(any(), any())
            ).willThrow(AuthWrongPasswordException("Wrong password."))

            // When & Then
            client.post()
                .uri(URI_AUTH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo("Wrong password.")
                .jsonPath("$.errorCode").isEqualTo("AUTH_WRONG_PASSWORD")
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    inner class ValidateTokenTest {

        @Test
        @DisplayName("토큰 검증 성공")
        fun `토큰 검증 성공`() {
            val email = "yologger1013@gmail.com"
            val nickname = "yologger1013"
            val name = "yologger1013"
            val accessToken = jwtUtil.createToken(JwtClaim(uid = UserResourceTest.UID))

            given(
                authService.validateToken(any(), any())
            ).willReturn(
                APIEnvelop(
                    data = ValidateAccessTokenResponse(
                        uid = UID,
                        accessToken = accessToken,
                        email = email,
                        nickname = nickname,
                        name = name
                    )
                )
            )

            // When & Then
            client.post()
                .uri(URI_AUTH_VALIDATE_TOKEN)
                .header(AuthData.USER_KEY, "$accessToken")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data.uid").isEqualTo(UserResourceTest.UID)
                .jsonPath("$.data.email").isEqualTo(email)
        }


        @Test
        @DisplayName("유효하지 않은 토큰 시, 401을 반환한다")
        fun `유효하지 않은 토큰 시, 401을 반환한다`() {
            val email = "yologger1013@gmail.com"
            val nickname = "yologger1013"
            val name = "yologger1013"
            val accessToken = jwtUtil.createToken(JwtClaim(uid = UserResourceTest.UID))

            given(
                authService.validateToken(any(), any())
            ).willReturn(
                APIEnvelop(
                    data = ValidateAccessTokenResponse(
                        uid = UID,
                        accessToken = accessToken,
                        email = email,
                        nickname = nickname,
                        name = name
                    )
                )
            )

            // When & Then
            client.post()
                .uri(URI_AUTH_VALIDATE_TOKEN)
                .header(AuthData.USER_KEY, "$accessToken" + "dummy")
                .exchange()
                .expectStatus().isUnauthorized
                .expectBody()
                .jsonPath("$.errorMessage").isEqualTo("Invalid token")
                .jsonPath("$.errorCode").isEqualTo("AUTH_INVALID_TOKEN")
        }
    }

    @Nested
    @DisplayName("로그아웃")
    inner class LogoutTest {

        @Test
        @DisplayName("로그아웃 성공 시 200을 반환한다")
        fun `로그아웃 성공 시 200을 반환한다`() {
            val email = "yologger1013@gmail.com"
            val nickname = "yologger1013"
            val name = "yologger1013"
            val accessToken = jwtUtil.createToken(JwtClaim(uid = UserResourceTest.UID))

            given(
                authService.logout(any(), any())
            ).willReturn(
                APIEnvelop(
                    data = LogoutResponse(uid = UID),
                )
            )

            // When & Then
            client.post()
                .uri(URI_AUTH_LOGOUT)
                .header(AuthData.USER_KEY, "$accessToken")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data.uid").isEqualTo(UID)
        }
    }

    companion object {
        const val URI_AUTH_LOGIN = "/api/ums/v1/auth/login"
        const val URI_AUTH_VALIDATE_TOKEN = "/api/ums/v1/auth/validate_token"
        const val URI_AUTH_LOGOUT = "/api/ums/v1/auth/logout"
        const val UID: Long = 235
    }
}