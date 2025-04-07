package link.yologram.api.domain.bms.dto.comment

data class CreateCommentRequest(
    val uid: Long,
    val content: String
)