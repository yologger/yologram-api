package link.yologram.api.domain.ums.entity

import jakarta.persistence.*
import link.yologram.api.domain.ums.enum.UserStatus
import org.springframework.data.annotation.CreatedDate
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name= "`user`")
class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "int unsigned")
    val id: Long = 0,

    @Column(name = "email", columnDefinition = "varchar(200) unique charset utf8mb4")
    var email: String,

    @Column(name = "name", columnDefinition = "varchar(200) charset utf8mb4")
    var name: String,

    @Column(name = "nickname", columnDefinition = "varchar(200) charset utf8mb4")
    var nickname: String,

    @Column(name = "password", columnDefinition = "varchar(200) charset utf8mb4")
    var password: String,

    @Column(name = "avatar", columnDefinition = "varchar(512) charset utf8mb4")
    var avatar: String? = null,

    @Column(name = "access_token", columnDefinition = "varchar(256) charset utf8mb4")
    var accessToken: String? = null,

    @Column(name = "status", columnDefinition = "varchar(15) default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.ACTIVE,

    @Column(name = "deleted_date")
    var deletedDate: LocalDateTime? = null
) {
    @Column(name = "joined_date")
    @CreatedDate
    lateinit var joinedDate: LocalDateTime

    @Column(name = "modified_date", columnDefinition = "timestamp on update CURRENT_TIMESTAMP")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @LastModifiedDate
    lateinit var modifiedDate: LocalDateTime
}