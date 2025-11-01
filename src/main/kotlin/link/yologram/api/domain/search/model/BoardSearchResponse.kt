package link.yologram.api.domain.search.model

import io.swagger.v3.oas.annotations.media.Schema
import link.yologram.api.domain.search.document.BoardDocument

@Schema(description = "게시글 검색 응답")
data class BoardSearchResponse(
    @field:Schema(description = "검색된 게시글 목록")
    val boards: List<BoardDocument>,

    @field:Schema(description = "전체 검색 결과 수")
    val total: Long,

    @field:Schema(description = "현재 페이지 (0부터 시작)")
    val page: Int,

    @field:Schema(description = "페이지 크기")
    val size: Int,

    @field:Schema(description = "총 페이지 수")
    val totalPages: Int = ((total + size - 1) / size).toInt(),

    @field:Schema(description = "마지막 페이지 여부")
    val isLast: Boolean = (page + 1) >= totalPages
)
