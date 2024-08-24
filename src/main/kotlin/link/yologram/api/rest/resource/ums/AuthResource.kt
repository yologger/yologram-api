package link.yologram.api.rest.resource.ums

import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ums/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class AuthResource {
}