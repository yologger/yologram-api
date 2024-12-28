package link.yologram.api.domain.auth.dto

data class AuthData(
    val uid: Long
) {
    companion object {
        const val USER_KEY = "X_YOLOGRAM_AUTH_TOKEN"
        const val SERVICE_KEY = "X_YOLOGRAM_MICROSERVICE-TOKEN"
    }
}