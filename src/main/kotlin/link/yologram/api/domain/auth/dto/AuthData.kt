package link.yologram.api.domain.auth.dto

data class AuthData(
    val uid: Long,
    val accessToken: String
) {
    companion object {
        const val USER_KEY = "X-YOLOGRAM-USER-AUTH-TOKEN"
        const val SERVICE_KEY = "X-YOLOGRAM-SERVICE-AUTH-TOKEN"
    }
}