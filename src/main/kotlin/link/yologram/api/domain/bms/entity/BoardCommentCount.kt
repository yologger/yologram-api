package link.yologram.api.domain.bms.entity

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@EntityListeners(AuditingEntityListener::class)
@Entity
@Table(name = "board_comment_count")
class BoardCommentCount(
    @Id
    @Column(nullable = false)
    val bid: Long,

    @Column(name = "count")
    @ColumnDefault("0")
    var count: Long = 0
)