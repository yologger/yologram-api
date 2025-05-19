package link.yologram.api.domain.ums.resource

import any
import link.yologram.api.domain.ums.model.JoinResponse
import link.yologram.api.common.AbstractWebMvcTest
import link.yologram.api.config.JwtConfig
import link.yologram.api.domain.ums.service.UserService
import link.yologram.api.domain.ums.util.JwtUtil
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.utlis.isCreated
import org.mockito.BDDMockito.given
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType

class UserResourceTest: AbstractWebMvcTest() {

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
    }

    companion object {
        const val URI_USER_JOIN = "/api/ums/v1/user/join"
        const val UID = 235L
    }
}