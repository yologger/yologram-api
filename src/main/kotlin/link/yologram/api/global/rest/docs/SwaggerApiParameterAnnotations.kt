package link.yologram.api.global.rest.docs

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(RUNTIME)
@Parameter(
    name = "X-YOLOGRAM-USER-AUTH-TOKEN",
    description = "사용자 인증 토큰",
    required = true,
    `in` = ParameterIn.HEADER,
    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
)
annotation class ApiParameterAuthToken