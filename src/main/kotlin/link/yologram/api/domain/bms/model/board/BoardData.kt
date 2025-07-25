package link.yologram.api.domain.bms.model.board

import link.yologram.api.domain.bms.entity.Board
import java.time.LocalDateTime

data class BoardData(
    val bid: Long,
    val uid: Long,
    val title: String,
    val content: String,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime,
) {
    data class Metrics (
        val commentCount: Long,
        val viewCount: Long,
    )

    companion object {
        fun fromEntity(board: Board): BoardData {
            return BoardData(
                bid = board.id,
                uid = board.uid,
                title = board.title,
                content = board.content,
                createdDate = board.createdDate,
                modifiedDate = board.modifiedDate
            )
        }
    }
}
