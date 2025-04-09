package link.yologram.api.domain.bms.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(
    name = "`board_like`",
    uniqueConstraints = [UniqueConstraint(columnNames = ["bid", "uid"])]  // bid와 uid를 복합 유니크 키로 설정
)
class BoardLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "int unsigned")
    val id: Long = 0,

    @Column(name = "bid", columnDefinition = "int unsigned")
    var bid: Long,

    @Column(name = "uid", columnDefinition = "int unsigned")
    var uid: Long
)