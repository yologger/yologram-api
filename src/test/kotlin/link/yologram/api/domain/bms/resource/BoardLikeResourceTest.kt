package link.yologram.api.domain.bms.resource

import link.yologram.api.common.WebMvcTestSupport
import link.yologram.api.domain.bms.model.LikeBoardResponse
import link.yologram.api.domain.bms.model.UnlikeBoardResponse
import link.yologram.api.domain.bms.service.BoardLikeService
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.domain.ums.model.JwtClaim
import link.yologram.api.domain.ums.resource.UserResourceTest.Companion.UID
import link.yologram.api.domain.ums.util.JwtUtil
import link.yologram.api.global.model.APIEnvelop
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.awt.PageAttributes
import kotlin.test.Test

@WebMvcTestSupport(controllers = [BoardLikeResource::class])
class BoardLikeResourceTest(
    @Autowired val client: WebTestClient,
    @Autowired val jwtUtil: JwtUtil,
) {
    @MockBean
    lateinit var boardLikeService: BoardLikeService

    @Nested
    @DisplayName("게시글 좋아요")
    inner class LikeBoardTest {

        @Test
        @DisplayName("게시글 좋아요 성공 시 200 반환한다")
        fun `게시글 좋아요 성공 시 200 반환한다`() {
            val bid = 10L
            val uid = 1L
            val accessToken = jwtUtil.createToken(JwtClaim(uid = UID))
            val response = APIEnvelop(data = LikeBoardResponse(uid, bid))

            given(boardLikeService.likeBoard(uid, bid)).willReturn(response)

            client.post()
                .uri("/api/bms/v1/board/like/{bid}", bid)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthData.USER_KEY, "$accessToken")
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @DisplayName("게시글 좋아요 취소")
    inner class UnlikeBoardTest {

        @Test
        @DisplayName("게시글 좋아요 취소 성공 시 200 반환한다")
        fun `게시글 좋아요 취소 성공 시 200 반환한다`() {
            val bid = 10L
            val uid = 1L
            val accessToken = jwtUtil.createToken(JwtClaim(uid = UID))
            val response = APIEnvelop(data = UnlikeBoardResponse(uid, bid))

            given(boardLikeService.unlikeBoard(uid, bid)).willReturn(response)

            client.method(HttpMethod.DELETE)
                .uri("/api/bms/v1/board/like/{bid}", bid)
                .header(AuthData.USER_KEY, "$accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
        }
    }
}