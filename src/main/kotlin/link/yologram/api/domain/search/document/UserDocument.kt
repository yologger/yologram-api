package link.yologram.api.domain.search.document

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import link.yologram.api.domain.ums.entity.User
import link.yologram.api.domain.ums.enum.UserStatus
import java.time.LocalDateTime

data class UserDocument(
    val id: Long = 0,
    val email: String,
    val name: String,
    val nickname: String,
    val password: String,
    val accessToken: String? = null,
    val status: UserStatus = UserStatus.ACTIVE,

    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    val joinedDate: LocalDateTime,

    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    val modifiedDate: LocalDateTime,

    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    val deletedDate: LocalDateTime? = null,
) {
    companion object {
        fun of(userEntity: User): UserDocument {
            return UserDocument(
                id = userEntity.id,
                email = userEntity.email,
                name = userEntity.name,
                nickname = userEntity.nickname,
                password = userEntity.password,
                accessToken = userEntity.accessToken,
                status = userEntity.status,
                joinedDate = userEntity.joinedDate,
                modifiedDate = userEntity.modifiedDate,
                deletedDate = userEntity.deletedDate
            )
        }
    }
}