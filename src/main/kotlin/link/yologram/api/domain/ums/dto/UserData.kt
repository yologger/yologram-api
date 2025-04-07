package link.yologram.api.domain.ums.dto

import link.yologram.api.domain.ums.entity.User
import java.time.LocalDateTime

data class UserData (
    val uid: Long,
    var email: String,
    var name: String,
    var nickname: String,
    var accessToken: String? = null,
    var joinDate: LocalDateTime
) {
    companion object {
        fun fromEntity(user: User): UserData {
            return UserData(
                uid = user.id,
                email = user.email,
                name = user.name,
                nickname = user.nickname,
                accessToken = user.accessToken,
                joinDate = user.joinDate
            )
        }
    }
}