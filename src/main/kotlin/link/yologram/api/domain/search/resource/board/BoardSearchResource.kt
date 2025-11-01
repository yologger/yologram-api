package link.yologram.api.domain.search.resource.board

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.search.model.BoardFindParams
import link.yologram.api.domain.search.model.BoardSearchParams
import link.yologram.api.domain.search.model.BoardSearchResponse
import link.yologram.api.domain.search.service.board.BoardSearchService
import link.yologram.api.global.model.APIEnvelopPage
import link.yologram.api.global.rest.docs.ApiResponseInvalidArgument
import link.yologram.api.global.rest.wrapOk
import org.slf4j.LoggerFactory
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "검색/게시글", description = "게시글 검색 엔드포인트 (search/BoardSearchResource)")
@RestController
@ApiResponseInvalidArgument
@RequestMapping("/api/search/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class BoardSearchResource(
    private val boardSearchService: BoardSearchService
) {

    private val logger = LoggerFactory.getLogger(BoardSearchResource::class.java)

    @Operation(
        summary = "게시글 검색",
        description = "키워드로 게시글을 검색한다/ 키워드가 없으면 전체 게시글을 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "유저 게시글 조회",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = BoardSearchResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                            "boards": [
                                {
                                    "id": 70,
                                    "uid": 32,
                                    "title": "This is title for testing aa",
                                    "content": "This is content for testing aa",
                                    "status": "ACTIVE",
                                    "createdDate": "2025-10-06T18:35:49Z",
                                    "modifiedDate": "2025-10-06T18:35:49Z",
                                    "metrics": {
                                        "commentCount": 0,
                                        "likeCount": 0,
                                        "viewCount": 0
                                    }
                                },
                                {
                                    "id": 69,
                                    "uid": 32,
                                    "title": "This is title for testing",
                                    "content": "This is content for testing",
                                    "status": "ACTIVE",
                                    "createdDate": "2025-10-06T18:35:41Z",
                                    "modifiedDate": "2025-10-06T18:35:41Z",
                                    "metrics": {
                                        "commentCount": 0,
                                        "likeCount": 0,
                                        "viewCount": 0
                                    }
                                }
                            ],
                            "total": 69,
                            "page": 0,
                            "size": 2,
                            "totalPages": 4,
                            "isLast": false
                        }"""
                    )
                ]
            )
        ]
    )
    @GetMapping("/boards")
    fun search(
        @ParameterObject @Valid findParams: BoardFindParams,
        @ParameterObject @Valid pageParams: BoardSearchParams
    ): ResponseEntity<BoardSearchResponse> {
        return boardSearchService.search(
            keyword = findParams.q,
            page = pageParams.page,
            size = pageParams.size ?: 20
        ).wrapOk()
    }
}