package link.yologram.api.domain.ums.dto

data class LoginResponse(
    val uid: Long,
    val accessToken: String,
    val email: String,
    val nickname: String,
    val name: String
)
