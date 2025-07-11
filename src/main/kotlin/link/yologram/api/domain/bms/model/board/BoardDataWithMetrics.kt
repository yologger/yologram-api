package link.yologram.api.domain.bms.model.board

import java.time.LocalDateTime

data class BoardDataWithMetrics(
    val bid: Long,
    val uid: Long,
    val title: String,
    val body: String,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime,
    val metrics: Metrics
) {
    data class Metrics (
        val commentCount: Long = 0,
        val likeCount: Long = 0,
        val viewCount: Long = 0,
    )
}
