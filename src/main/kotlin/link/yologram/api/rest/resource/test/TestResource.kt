package link.yologram.api.rest.resource.test

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.auth.dto.AuthData
import link.yologram.api.rest.response.JsonPayload
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/test/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class TestResource {

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