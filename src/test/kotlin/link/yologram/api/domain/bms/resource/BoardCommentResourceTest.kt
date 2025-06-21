package link.yologram.api.domain.bms.resource

import link.yologram.api.common.WebMvcTestSupport
import link.yologram.api.domain.bms.model.comment.CommentData
import link.yologram.api.domain.bms.model.comment.CreateCommentRequest
import link.yologram.api.domain.bms.service.BoardCommentService
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.domain.ums.model.JwtClaim
import link.yologram.api.domain.ums.util.JwtUtil
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.model.APIEnvelopPage
import org.junit.jupiter.api.DisplayName
import org.mockito.BDDMockito.given
import org.mockito.Mockito.doNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import kotlin.test.Test

@WebMvcTestSupport(controllers = [BoardCommentResource::class])
class BoardCommentResourceTest(
    @Autowired val client: WebTestClient,
    @Autowired val jwtUtil: JwtUtil
) {
    @MockBean
    lateinit var boardCommentService: BoardCommentService

    @Test
    @DisplayName("댓글 작성 성공 시 201 반환한다")
    fun `댓글 작성 성공 시 201을 반환한다`() {
        val uid = 1L
        val bid = 1L
        val accessToken = jwtUtil.createToken(JwtClaim(uid = uid))
        val authData = AuthData(uid = 10L, accessToken = accessToken)
        val request = CreateCommentRequest(content = "test comment")
        val commentData = CommentData(
            id = 100L,
            uid = authData.uid,
            bid = bid,
            content = request.content,
            createdDate = LocalDateTime.now(),
            modifiedDate = LocalDateTime.now()
        )

        given(boardCommentService.createComment(bid, authData.uid, request.content))
            .willReturn(APIEnvelop(data = commentData))

        client.post()
            .uri("/api/bms/v1/board/$bid/comment")
            .header(AuthData.USER_KEY, "$accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
    }

    @Test
    @DisplayName("댓글 삭제 성공 시 204을 반환한다")
    fun `댓글 삭제 성공 시 204을 반환한다`() {
        val uid = 1L
        val bid = 1L
        val cid = 1L
        val accessToken = jwtUtil.createToken(JwtClaim(uid = uid))

        doNothing().`when`(boardCommentService).deleteComment(bid, cid)

        client.method(HttpMethod.DELETE)
            .uri("/api/bms/v1/board/$bid/comment/$cid")
            .header(AuthData.USER_KEY, "$accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    @DisplayName("댓글 목록 조회 성공 시 200을 반환한다")
    fun `댓글 목록 조회 성공 시 200을 반환한다`() {
        val bid = 1L
        val commentData = CommentData(
            id = 1L,
            uid = 10L,
            bid = bid,
            content = "Great post!",
            createdDate = LocalDateTime.now(),
            modifiedDate = LocalDateTime.now()
        )
        given(boardCommentService.getCommentsByBid(bid))
            .willReturn(APIEnvelopPage(data = listOf(commentData)))

        client.get()
            .uri("/api/bms/v1/board/$bid/comments")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.data.length()").isEqualTo(1)
    }
}