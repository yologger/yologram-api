package link.yologram.api.rest.resource.test

import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class TestResource {

    @GetMapping("/test")
    fun test(): String {
        return "test"
    }
}