package link.yologram.api.domain.bms.resource

import link.yologram.api.common.WebMvcTestSupport
import link.yologram.api.domain.bms.service.BoardViewService
import link.yologram.api.domain.ums.util.JwtUtil
import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test

@WebMvcTestSupport(
    controllers = [BoardViewResource::class]
)
class BoardViewResourceTest(
    @Autowired val jwtUtil: JwtUtil,
    @Autowired var client: WebTestClient
) {
    @MockBean
    lateinit var boardViewService: BoardViewService

    @Test
    @DisplayName("조회 기록 저장 성공 시 204 NoContent 반환한다")
    fun `조회 기록 저장 성공 시 204 NoContent 반환한다`() {
        val bid = 100L
        val request = mapOf("uid" to 1L)

        client.post()
            .uri("/api/bms/v1/board/$bid/view")
            .header("X-Forwarded-For", "127.0.0.1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNoContent

        verify(boardViewService).recordView(boardId = bid, uid = 1L, ip = "127.0.0.1")
    }

    @Test
    @DisplayName("로그안하지 않는 유저(uid가 없는 경우)도 정상적으로 기록된다")
    fun `로그안하지 않는 유저(uid가 없는 경우)도 정상적으로 기록된다`() {
        val bid = 101L

        client.post()
            .uri("/api/bms/v1/board/$bid/view")
            .header("X-Forwarded-For", "192.168.0.10")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent

        verify(boardViewService).recordView(boardId = bid, uid = null, ip = "192.168.0.10")
    }
}