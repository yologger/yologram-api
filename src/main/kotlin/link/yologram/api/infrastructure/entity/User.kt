package link.yologram.api.infrastructure.entity

import jakarta.persistence.*
import link.yologram.api.infrastructure.enum.UserStatus
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

    @Column(name = "access_token", columnDefinition = "varchar(256) charset utf8mb4")
    var accessToken: String? = null,

    @Column(name = "status", columnDefinition = "varchar(15) default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.ACTIVE,

    @Column(name = "deleted_date")
    var deletedDate: LocalDateTime? = null
) {
    @Column(name = "join_date")
    @CreatedDate
    lateinit var joinDate: LocalDateTime

    @Column(name = "modified_date", columnDefinition = "timestamp on update CURRENT_TIMESTAMP")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @LastModifiedDate
    lateinit var modifiedDate: LocalDateTime
}