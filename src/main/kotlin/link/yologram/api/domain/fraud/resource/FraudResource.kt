package link.yologram.api.domain.fraud.resource

import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.fraud.model.FraudErrorResponse
import link.yologram.api.domain.fraud.model.IpInfoResponse
import link.yologram.api.domain.fraud.service.IpDomainService
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.rest.docs.ApiResponseInvalidArgument
import link.yologram.api.global.rest.wrapOk
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "사기탐지", description = "사기탐지 관련 엔드포인트 (fraud/FraudResource)")
@ApiResponseInvalidArgument
@RestController
@RequestMapping("/api/fraud/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class FraudResource(
    private val ipDomainService: IpDomainService
) {
    private val logger = KotlinLogging.logger { }

    @Operation(summary = "ip 상세정보 조회", description = "ip로 상세정보를 조회한다..")
    @GetMapping("/admin/ip-info")
    @ApiResponse(
        responseCode = "200",
        description = "ip 상세정보 조회 성공",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = IpInfoResponse::class),
            )
        ]
    )
    @ApiResponse(
        responseCode = "500",
        description = "알 수 없는 에러",
        content = [
            Content(
                mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
                schema = Schema(implementation = FraudErrorResponse::class),
            )
        ]
    )
    fun getIpInfo(
        @RequestParam ip: String
    ): ResponseEntity<APIEnvelop<IpInfoResponse>> {
        return ipDomainService.getIpInfo(ip).wrapOk()
    }
}