package link.yologram.api.rest.resource.ums

import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.ums.UserService
import link.yologram.api.domain.ums.dto.JoinRequest
import link.yologram.api.rest.support.wrapCreated
import link.yologram.api.rest.support.wrapOk
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ums/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class UserResource(
    private val userService: UserService
) {
    @PostMapping("/join", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun join(@Validated @RequestBody request: JoinRequest) = userService.join(request).wrapCreated()

    @GetMapping("/user/{uid}")
    fun getUser(@PathVariable(name = "uid") uid: Long) = userService.getUser(uid).wrapOk()

    @DeleteMapping("/withdraw", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun withdraw() {

    }
}