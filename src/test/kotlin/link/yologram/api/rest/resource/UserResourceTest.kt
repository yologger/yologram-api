package link.yologram.api.rest.resource

import any
import link.yologram.api.domain.ums.dto.JoinResponse
import link.yologram.api.rest.AbstractWebMvcTest
import link.yologram.api.rest.isCreated
import org.mockito.BDDMockito.given
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class UserResourceTest: AbstractWebMvcTest() {

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
            ).willReturn(JoinResponse(1))

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
        const val URI_USER_JOIN = "/api/ums/v1/join"
        const val UID = 235L
    }
}