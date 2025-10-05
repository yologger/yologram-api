package link.yologram.api.global.rest.docs

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import link.yologram.api.config.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(CLASS, FUNCTION)
@Retention(RUNTIME)
@ApiResponse(
    responseCode = "400",
    description = "유효하지 않은 입력값",
    content = [
        Content(
            mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
            schema = Schema(
                example = "{ \"errorMessage\": \"(에러 메시지)\", \"errorCode\": \"(에러 코드)\" }"
            ),
            examples = [
                ExampleObject(
                    value = """{
                        "errorMessage": "(에러 메시지)",
                        "errorCode": "HTTP_REQUEST_ARGUMENT_INVALID"    
                    }"""
                )
            ]
        )
    ]
)
//@ApiResponse(
//    responseCode = "405",
//    description = "잘못된 HTTP Method",
//    content = [
//        Content(
//            mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
//            schema = Schema(
//                example = "{ \"errorMessage\": \"(에러 메시지)\", \"errorCode\": \"(에러 코드)\" }"
//            ),
//            examples = [
//                ExampleObject(
//                    value = """{
//                        "errorMessage": "Http Request Method Not Allowed",
//                        "errorCode": "HTTP_REQUEST_METHOD_NOT_ALLOWED"
//                    }"""
//                )
//            ]
//        )
//    ]
//)
annotation class ApiResponseInvalidArgument

@ApiResponse(
    responseCode = "401",
    description = "인증 실패",
    content = [
        Content(
            mediaType = MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE,
            schema = Schema(
                example = "{ \"errorMessage\": \"(에러 메시지)\", \"errorCode\": \"(에러 코드)\" }"
            ),
            examples = [
                ExampleObject(
                    name = "expiredToken", value = "{ \"errorMessage\": \"expired token\", \"errorCode\": \"AUTH_EXPIRED_TOKEN\" }", description = "토큰이 만료됨"
                ),
                ExampleObject(
                    name = "invalidToken", value = "{ \"errorMessage\": \"invalid token\", \"errorCode\": \"AUTH_INVALID_TOKEN\" }", description = "토큰이 유효하지 않음"
                ),
                ExampleObject(
                    name = "invalidTokenOwner", value = "{ \"errorMessage\": \"Invalid token owner\", \"errorCode\": \"AUTH_INVALID_TOKEN_OWNER\" }", description = "토큰의 owner가 아님"
                )
            ]
        )
    ]
)
annotation class ApiResponseUnauthorized