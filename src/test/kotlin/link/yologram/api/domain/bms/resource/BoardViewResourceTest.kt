package link.yologram.api.domain.bms.resource

import link.yologram.api.common.WebMvcTestSupport
import link.yologram.api.domain.bms.service.BoardViewService
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.domain.ums.model.JwtClaim
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
    @DisplayName("access token이 있는 경우, 조회 기록 저장 성공 시 204 NoContent 반환한다")
    fun `조회 기록 저장 성공 시 204 NoContent 반환한다`() {

        val uid = 2L
        val accessToken = jwtUtil.createToken(JwtClaim(uid = uid))
        val authData = AuthData(uid = 10L, accessToken = accessToken)
        val bid = 1L
        val ip = "127.0.0.1"


        client.post()
            .uri("/api/bms/v1/board/$bid/view")
            .header("X-Forwarded-For", "127.0.0.1")
            .header(AuthData.USER_KEY, "$accessToken")
            .exchange()
            .expectStatus().isNoContent

        verify(boardViewService).recordView(boardId = bid, uid = uid, ip = "127.0.0.1")
    }

    @Test
    @DisplayName("access token이 없는 경우에도, 조회 기록 저장 성공 시 204 NoContent 반환한다\")")
    fun `로그안하지 않는 유저(uid가 없는 경우)도 정상적으로 기록된다`() {
        val bid = 101L

        client.post()
            .uri("/api/bms/v1/board/$bid/view")
            .header("X-Forwarded-For", "192.168.0.10")
            .exchange()
            .expectStatus().isNoContent

        verify(boardViewService).recordView(boardId = bid, uid = null, ip = "192.168.0.10")
    }
}