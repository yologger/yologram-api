package link.yologram.api.domain.search.document

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import link.yologram.api.domain.bms.enum.BoardStatus
import link.yologram.api.domain.bms.model.board.BoardDataWithMetrics
import java.time.LocalDateTime

data class BoardDocument(
    val id: Long = 0,
    val uid: Long,
    val title: String,
    val content: String,
    var status: BoardStatus = BoardStatus.ACTIVE,

    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    val createdDate: LocalDateTime,

    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    val modifiedDate: LocalDateTime,

    val metrics: Metrics
) {
    data class Metrics (
        val commentCount: Long,
        val likeCount: Long,
        val viewCount: Long,
    )

    companion object {
        fun of(board: BoardDataWithMetrics): BoardDocument {
            return BoardDocument(
                id = board.bid,
                uid = board.writer.uid,
                title = board.title,
                content = board.content,
                createdDate = board.createdDate,
                modifiedDate = board.modifiedDate,
                metrics = Metrics(
                    commentCount = board.metrics.commentCount,
                    likeCount = board.metrics.likeCount,
                    viewCount = board.metrics.viewCount
                )
            )
        }
    }
}
