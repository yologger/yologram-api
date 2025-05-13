package link.yologram.api.domain.test.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.test.model.EchoResponse
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.domain.ums.model.UserData
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.rest.wrapOk
import link.yologram.api.infra.cache.CacheService
import link.yologram.api.infra.cache.Cache
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@Tag(name = "테스트", description = "테스트 관련 엔드포인트 (test/TestResource)")
@RestController
@RequestMapping("/api/test/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class TestResource(
    private val cacheService: CacheService
) {

    private val logger = LoggerFactory.getLogger(TestResource::class.java)

    @Operation(
        summary = "test 엔드포인트",
        description = "문자열 'test'를 반환합니다.",
    )
    @GetMapping("/test")
    fun test(): String {
        return "test"
    }

    @Autowired
    private lateinit var request: HttpServletRequest

    @RequestMapping(
        value = ["/echo/**"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
        method = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS]
    )
    fun echoBack(@RequestBody(required = false) rawBody: ByteArray?): ResponseEntity<APIEnvelop<EchoResponse>> {

        val headers: Map<String, String> = Collections.list(request.headerNames).associateWith { request.getHeader(it) }

        val host = mutableMapOf<String, String>().also {
            it["ip"] = request.remoteAddr
            it["hostname"] = request.remoteHost
        }

        logger.info("host: $host")
        logger.info("protocol: ${request.protocol ?: ""}")
        logger.info("method: ${request.method ?: ""}")
        logger.info("headers: $headers")
        logger.info("cookies: ${request.cookies ?: emptyArray<Cookie>()}")
        logger.info("parameters: ${request.parameterMap}")
        logger.info("querystring: ${request.queryString ?: ""}")
        logger.info("path: ${request.servletPath}")
        logger.info("rawBody: ${rawBody?.let { Base64.getEncoder().encodeToString(it) } ?: ""}")

        val response = EchoResponse().apply {
            set(EchoResponse.HOST, host)
            set(EchoResponse.PROTOCOL, request.protocol ?: "")
            set(EchoResponse.METHOD, request.method ?: "")
            set(EchoResponse.HEADERS, headers)
            set(EchoResponse.COOKIES, request.cookies ?: emptyArray<Cookie>() )
            set(EchoResponse.PARAMETERS, request.parameterMap)
            set(EchoResponse.QUERYSTRING, request.queryString ?: "")
            set(EchoResponse.PATH, request.servletPath)
            set(EchoResponse.BODY, rawBody?.let { Base64.getEncoder().encodeToString(it) } ?: "")
        }

        return APIEnvelop(data = response).wrapOk()
    }

    @GetMapping("/auth_necessary")
    fun authNecessary(
        authData: AuthData
    ): ResponseEntity<APIEnvelop<String>> {
        println(authData)
        return APIEnvelop(data = "auth_necessary").wrapOk()
    }

    @GetMapping("/auth_not_necessary")
    fun authNotNecessary(): ResponseEntity<APIEnvelop<String>> {
        return APIEnvelop(data = "auth_not_necessary").wrapOk()
    }

//    @PostMapping("/redis")
//    fun saveDataToRedis() {
//        cacheService.set(Cache.user(1), UserData(uid = 1, email = "sample@gmail.com", "sample", "samlple")
//    }
//
//    @GetMapping("/redis")
//    fun getDataFromRedis() {
//        cacheService.getOrNull(Cache)
//    }
}