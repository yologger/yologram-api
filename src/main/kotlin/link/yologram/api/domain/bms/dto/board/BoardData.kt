package link.yologram.api.domain.bms.dto.board

import link.yologram.api.domain.bms.entity.Board
import java.time.LocalDateTime

data class BoardData(
    val bid: Long,
    val uid: Long,
    val title: String,
    val body: String,
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
                body = board.body,
                createdDate = board.createdDate,
                modifiedDate = board.modifiedDate
            )
        }
    }
}
