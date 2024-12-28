package link.yologram.api.domain.auth.resource

import link.yologram.api.domain.ums.dto.LogoutRequest
import link.yologram.api.domain.ums.dto.ValidateAccessTokenRequest
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.auth.dto.AuthData
import link.yologram.api.domain.auth.service.AuthService
import link.yologram.api.domain.ums.dto.LoginRequest
import link.yologram.api.domain.ums.dto.LoginResponse
import link.yologram.api.rest.support.Response
import link.yologram.api.rest.support.wrapOk
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ums/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class AuthResource(
    private val authService: AuthService
) {

    @PostMapping("/login", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun login(@Validated @RequestBody request: LoginRequest): ResponseEntity<Response<LoginResponse>> {
         return authService.login(request.email, request.password).wrapOk()
    }

    @PostMapping("/validate_token", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun validateToken(@Validated @RequestBody request: ValidateAccessTokenRequest)
    = authService.validateToken(uid = request.uid, accessToken = request.accessToken).wrapOk()

    @PostMapping("/logout", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun logout(
        @RequestHeader(AuthData.USER_KEY, required = true) accessToken: String,
        @Validated @RequestBody request: LogoutRequest
    ) = authService.logout(uid = request.uid, accessToken = accessToken)
}