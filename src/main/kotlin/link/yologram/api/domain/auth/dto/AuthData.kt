package link.yologram.api.domain.auth.dto

data class AuthData(
    val uid: Long,
    val accessToken: String
) {
    companion object {
        const val USER_KEY = "X_YOLOGRAM_USER_AUTH_TOKEN"
        const val SERVICE_KEY = "X_YOLOGRAM_SERVICE_AUTH_TOKEN"
    }
}