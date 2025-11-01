package link.yologram.api.domain.search.resource.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.search.service.user.UserIndexingService
import link.yologram.api.global.rest.docs.ApiResponseInvalidArgument
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "검색/유저인덱싱", description = "유저 인덱싱 엔드포인트 (search/UserIndexingResource)")
@RestController
@ApiResponseInvalidArgument
@RequestMapping("/api/search/v1/internal/indexing/user", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class UserIndexingResource(
    private val userIndexingService: UserIndexingService
) {

    private val logger = LoggerFactory.getLogger(UserIndexingResource::class.java)

    @Operation(summary = "유조 단건 인덱싱", description = "DB에서 uid로 유저 단건 조회 후, Opensearch에 인덱싱한다.")
    @ApiResponse(
        responseCode = "200",
        description = "인덱싱 완료",
    )
    @PutMapping("/{uid}")
    fun index(@PathVariable uid: Long) {
        userIndexingService.index(uid)
    }
}