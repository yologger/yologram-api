package link.yologram.api.rest.resource.ums

import link.yologram.api.domain.ums.dto.LogoutRequest
import link.yologram.api.domain.ums.dto.ValidateAccessTokenRequest
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.AUTH_TOKEN_KEY
import link.yologram.api.domain.ums.AuthService
import link.yologram.api.domain.ums.dto.LoginRequest
import link.yologram.api.rest.support.wrapOk
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ums/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class AuthResource(
    private val authService: AuthService
) {

    @PostMapping("/login", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun login(@Validated @RequestBody request: LoginRequest) = authService.login(request.email, request.password).wrapOk()

    @PostMapping("/validate_token", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun validateToken(@Validated @RequestBody request: ValidateAccessTokenRequest) = authService.validateToken(uid = request.uid, accessToken = request.accessToken).wrapOk()

    @PostMapping("/logout", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun logout(
        @RequestHeader(AUTH_TOKEN_KEY, required = true) accessToken: String,
        @Validated @RequestBody request: LogoutRequest
    ) = authService.logout(uid = request.uid, accessToken = accessToken)
}