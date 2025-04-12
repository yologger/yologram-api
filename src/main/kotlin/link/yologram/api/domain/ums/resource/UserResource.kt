package link.yologram.api.domain.ums.resource

import io.swagger.v3.oas.annotations.tags.Tag
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.auth.dto.AuthData
import link.yologram.api.domain.ums.service.UserService
import link.yologram.api.domain.ums.dto.JoinRequest
import link.yologram.api.global.wrapCreated
import link.yologram.api.global.wrapOk
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "유저", description = "유저 관련 엔드포인트 (user/UserResource)")
@RestController
@RequestMapping("/api/ums/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class UserResource(
    private val userService: UserService
) {
    @PostMapping("/join", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun join(@Validated @RequestBody request: JoinRequest) = userService.join(request).wrapCreated()

    @GetMapping("/user/{uid}")
    fun getUser(
        authData: AuthData,
        @PathVariable(name = "uid") uid: Long
    ) = userService.getUser(uid).wrapOk()

    @DeleteMapping("/user/withdraw")
    fun withdraw(
        authData: AuthData
    ) = userService.withdraw(authData.uid).wrapOk()
}