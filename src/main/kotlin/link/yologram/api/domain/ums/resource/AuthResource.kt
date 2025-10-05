package link.yologram.api.domain.ums.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.domain.ums.service.AuthService
import link.yologram.api.domain.ums.model.*
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.global.rest.docs.ApiParameterAuthTokenRequired
import link.yologram.api.global.rest.docs.ApiResponseInvalidArgument
import link.yologram.api.global.rest.docs.ApiResponseUnauthorized
import link.yologram.api.global.rest.wrapOk
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "유저/인증", description = "유저/인증 관련 엔드포인트 (ums/auth/AuthResource)")
@RestController
@ApiResponseInvalidArgument
@RequestMapping("/api/ums/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class AuthResource(
    private val authService: AuthService
) {

    @Operation(summary = "로그인", description = "email, password로 로그인 한다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공 후 인증 토큰을 발급받는다")
    @ApiResponse(
        responseCode = "404",
        description = "유저가 존재하지 않는다.",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = UmsErrorResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                        "errorMessage": "User not found",
                        "errorCode": "USER_NOT_FOUND"    
                    }"""
                    )
                ]
            )
        ]
    )
    @ApiResponse(
        responseCode = "401",
        description = "비밀번호가 틀렸다.",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = UmsErrorResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                        "errorMessage": "Wrong password",
                        "errorCode": "AUTH_WRONG_PASSWORD"    
                    }"""
                    )
                ]
            )
        ]
    )
    @PostMapping("/auth/login", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun login(@Validated @RequestBody request: LoginRequest): ResponseEntity<APIEnvelop<LoginResponse>> {
         return authService.login(request.email, request.password).wrapOk()
    }

    @Operation(summary = "액세스 토큰 검증", description = "액세스 토큰의 유효성을 검증한다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공 후 인증 토큰을 발급받는다")
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 user",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = UmsErrorResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                            "errorMessage": "User not found",
                            "errorCode": "USER_NOT_FOUND"
                        }"""
                    )
                ]
            )
        ]
    )
    @PostMapping("/auth/validate_token")
    fun validateToken(
        @Validated @RequestBody request: ValidateAccessTokenRequest
    ): ResponseEntity<APIEnvelop<ValidateAccessTokenResponse>>
    = authService.validateToken(request.accessToken).wrapOk()

    @Operation(summary = "로그아웃", description = "access token으로 로그아웃한다.")
    @ApiParameterAuthTokenRequired
    @ApiResponseUnauthorized
    @ApiResponse(responseCode = "200", description = "로그아웃에 성공한다.")
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 user",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = UmsErrorResponse::class),
                examples = [
                    ExampleObject(
                        value = """{
                            "errorMessage": "User not found",
                            "errorCode": "USER_NOT_FOUND"    
                        }"""
                    )
                ]
            )
        ]
    )
    @PostMapping("/auth/logout")
    fun logout(
        @Parameter(hidden = true) authData: AuthData
    ): ResponseEntity<APIEnvelop<LogoutResponse>> {
        return authService.logout(uid = authData.uid, accessToken = authData.accessToken).wrapOk()
    }
}