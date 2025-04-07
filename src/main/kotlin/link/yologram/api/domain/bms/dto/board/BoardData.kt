package link.yologram.api.domain.bms.dto.board

import link.yologram.api.domain.bms.entity.Board
import java.time.LocalDateTime

data class BoardData(
    val bid: Long,
    val uid: Long,
    val title: String,
    val body: String,
    val createDate: LocalDateTime,
    val modifiedDate: LocalDateTime
) {
    companion object {
        fun fromEntity(board: Board): BoardData {
            return BoardData(
                bid = board.id,
                uid = board.uid,
                title = board.title,
                body = board.body,
                createDate = board.createDate,
                modifiedDate = board.modifiedDate
            )
        }
    }
}
