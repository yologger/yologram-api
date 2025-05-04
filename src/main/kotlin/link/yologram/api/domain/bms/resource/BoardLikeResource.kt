package link.yologram.api.domain.bms.resource

import io.swagger.v3.oas.annotations.tags.Tag
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.bms.model.LikeBoardResponse
import link.yologram.api.domain.bms.model.UnlikeBoardResponse
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.domain.bms.service.BoardLikeService
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.rest.wrapOk
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "게시글/좋아요", description = "게시글/좋아요 관련 엔드포인트 (bms/BoardLikeResource)")
@RestController
@RequestMapping("/api/bms/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class BoardLikeResource(
    private val boardLikeService: BoardLikeService
) {

    @PostMapping("/board/like/{bid}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun likeBoard(
        @PathVariable(name = "bid") bid: Long,
        authData: AuthData
    ): ResponseEntity<APIEnvelop<LikeBoardResponse>> {
        return boardLikeService.likeBoard(uid = authData.uid, bid = bid).wrapOk()
    }

    @DeleteMapping("/board/like/{bid}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun unlikeBoard(
        @PathVariable(name = "bid") bid: Long,
        authData: AuthData
    ): ResponseEntity<APIEnvelop<UnlikeBoardResponse>> {
        return boardLikeService.unlikeBoard(uid = authData.uid, bid = bid).wrapOk()
    }
}