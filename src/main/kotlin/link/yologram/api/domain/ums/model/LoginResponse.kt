package link.yologram.api.domain.ums.model

data class LoginResponse(
    val uid: Long,
    val accessToken: String,
    val email: String,
    val nickname: String,
    val name: String
)
