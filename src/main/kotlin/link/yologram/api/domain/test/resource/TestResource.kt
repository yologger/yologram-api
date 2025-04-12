package link.yologram.api.domain.test.resource

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.auth.dto.AuthData
import link.yologram.api.domain.test.dto.JsonPayload
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@Tag(name = "테스트", description = "테스트 관련 엔드포인트 (test/TestResource)")
@RestController
@RequestMapping("/api/test/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class TestResource {

    private val logger = LoggerFactory.getLogger(TestResource::class.java)

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
    fun echoBack(@RequestBody(required = false) rawBody: ByteArray?): ResponseEntity<JsonPayload> {

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

        val response = JsonPayload().apply {
            set(JsonPayload.HOST, host)
            set(JsonPayload.PROTOCOL, request.protocol ?: "")
            set(JsonPayload.METHOD, request.method ?: "")
            set(JsonPayload.HEADERS, headers)
            set(JsonPayload.COOKIES, request.cookies ?: emptyArray<Cookie>() )
            set(JsonPayload.PARAMETERS, request.parameterMap)
            set(JsonPayload.QUERYSTRING, request.queryString ?: "")
            set(JsonPayload.PATH, request.servletPath)
            set(JsonPayload.BODY, rawBody?.let { Base64.getEncoder().encodeToString(it) } ?: "")
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    @GetMapping("/auth_necessary")
    fun authNecessary(
        authData: AuthData
    ): String {
        println(authData)
        return "auth_necessary"
    }

    @GetMapping("/auth_not_necessary")
    fun authNotNecessary(): String {
        return "auth_not_necessary"
    }
}