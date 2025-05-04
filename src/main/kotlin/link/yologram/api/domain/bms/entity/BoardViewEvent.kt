package link.yologram.api.domain.bms.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "board_view_event")
class BoardViewEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val bid: Long,

    val uid: Long? = null,

    val ip: String? = null,
) {
    @Column(name = "viewed_at")
    @CreatedDate
    lateinit var viewedAt: LocalDateTime
}