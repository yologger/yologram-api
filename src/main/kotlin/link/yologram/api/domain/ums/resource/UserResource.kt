package link.yologram.api.domain.ums.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema

import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import link.yologram.api.domain.ums.model.AuthData
import link.yologram.api.domain.ums.service.UserService
import link.yologram.api.domain.ums.model.JoinRequest
import link.yologram.api.domain.ums.model.UmsErrorResponse
import link.yologram.api.global.rest.wrapCreated
import link.yologram.api.global.rest.wrapOk
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "유저", description = "유저 관련 엔드포인트 (ums/user/UserResource)")
@RestController
@RequestMapping("/api/ums/v1", produces = [MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE])
class UserResource(
    private val userService: UserService
) {
    @Operation(
        summary = "회원가입",
        description = "email, name, nickname, password로 회원가입을 한다.",
        responses = [
            ApiResponse(
                responseCode = "409",
                description = "이미 회원가입한 유저",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UmsErrorResponse::class),
                        examples = [
                            ExampleObject(
                                value = """{
                                    "errorMessage": "user 'test@gmail.com' already exists.",
                                    "errorCode": "USER_DUPLICATE"    
                                }"""
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(responseCode = "204", description = "회원가입 성공")
        ]
    )
    @PostMapping("/user/join", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun join(@Validated @RequestBody request: JoinRequest) = userService.join(request).wrapCreated()

    @Operation(
        summary = "유저 조회",
        description = "uid로 유저를 조회한다.",
    )
    @GetMapping("/user/{uid}")
    fun getUser(
        authData: AuthData,
        @Parameter(description = "조회할 user의 uid", required = true) @PathVariable(name = "uid") uid: Long
    ) = userService.getUser(uid).wrapOk()


    @Operation(
        summary = "회원 탈퇴",
        description = "uid, access token으로 탈퇴를 진행한다."
    )
    @DeleteMapping("/user/withdraw")
    fun withdraw(
        authData: AuthData
    ) = userService.withdraw(authData.uid).wrapOk()
}