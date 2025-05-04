package link.yologram.api.domain.ums.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "유저 토큰 검증을 위한 모델")
data class AuthData(
    val uid: Long,
    val accessToken: String
) {
    companion object {
        const val USER_KEY = "X-YOLOGRAM-USER-AUTH-TOKEN"
        const val SERVICE_KEY = "X-YOLOGRAM-SERVICE-AUTH-TOKEN"
    }
}