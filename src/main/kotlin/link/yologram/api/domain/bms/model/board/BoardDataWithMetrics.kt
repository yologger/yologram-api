package link.yologram.api.domain.bms.model.board

import java.time.LocalDateTime

data class BoardDataWithMetrics(
    val bid: Long,
    val title: String,
    val content: String,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime,
    val writer: Writer,
    val metrics: Metrics
) {
    data class Writer (
        val uid: Long,
        val name: String,
        val nickname: String,
        val avatar: String?,
    )

    data class Metrics (
        val commentCount: Long = 0,
        val likeCount: Long = 0,
        val viewCount: Long = 0,
    )
}
