package link.yologram.api.domain.auth.resource

import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.auth.dto.AuthData
import link.yologram.api.domain.auth.service.AuthService
import link.yologram.api.domain.ums.dto.*
import link.yologram.api.global.Response
import link.yologram.api.global.wrapOk
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class AuthResource(
    private val authService: AuthService
) {

    @PostMapping("/login", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun login(@Validated @RequestBody request: LoginRequest): ResponseEntity<Response<LoginResponse>> {
         return authService.login(request.email, request.password).wrapOk()
    }

    @PostMapping("/validate_token")
    fun validateToken(authData: AuthData): ResponseEntity<Response<ValidateAccessTokenResponse>>
    = authService.validateToken(authData.accessToken, authData.uid).wrapOk()

    @PostMapping("/logout")
    fun logout(authData: AuthData)
    = authService.logout(uid = authData.uid, accessToken = authData.accessToken).wrapOk()
}