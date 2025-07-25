package link.yologram.api.domain.bms.entity

import jakarta.persistence.*
import link.yologram.api.domain.bms.enum.BoardStatus
import org.springframework.data.annotation.CreatedDate
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name= "`board`")
class Board (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "int unsigned")
    val id: Long = 0,

    @Column(name = "uid", columnDefinition = "int unsigned")
    var uid: Long,

    @Column(name = "title", columnDefinition = "varchar(256)")
    var title: String,

    @Column(name = "content", columnDefinition = "text")
    var content: String,

    @Column(name = "status", columnDefinition = "varchar(15) default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    var status: BoardStatus = BoardStatus.ACTIVE,

    @Column(name = "deleted_date")
    var deletedDate: LocalDateTime? = null
) {
    @Column(name = "created_date")
    @CreatedDate
    lateinit var createdDate: LocalDateTime

    @Column(name = "modified_date", columnDefinition = "timestamp on update CURRENT_TIMESTAMP")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @LastModifiedDate
    lateinit var modifiedDate: LocalDateTime
}