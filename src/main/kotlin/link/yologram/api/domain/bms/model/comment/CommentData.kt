package link.yologram.api.domain.bms.model.comment

import link.yologram.api.domain.bms.entity.Comment
import java.time.LocalDateTime

data class CommentData(
    val id: Long,
    val bid: Long,
    val uid: Long, // comment writer
    val content: String,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime
) {
    companion object {
        fun fromEntity(entity: Comment): CommentData {
            return CommentData(
                id = entity.id,
                bid = entity.bid,
                uid = entity.uid,
                content = entity.content,
                createdDate = entity.createdDate,
                modifiedDate = entity.modifiedDate
            )
        }
    }
}