package link.yologram.api.domain.bms.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "`comment`")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "int unsigned")
    val id: Long = 0,

    @Column(name = "uid", columnDefinition = "int unsigned")
    val uid: Long,

    @Column(name = "bid", columnDefinition = "int unsigned")
    val bid: Long,

    @Column(name = "content", columnDefinition = "text")
    val content: String,
) {
    @Column(name = "created_date")
    @CreatedDate
    lateinit var createdDate: LocalDateTime

    @Column(name = "modified_date", columnDefinition = "timestamp on update CURRENT_TIMESTAMP")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @LastModifiedDate
    lateinit var modifiedDate: LocalDateTime
}
