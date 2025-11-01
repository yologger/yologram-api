package link.yologram.api.domain.search.model

import io.swagger.v3.oas.annotations.Parameter

data class BoardSearchParams(
    @field:Parameter(description = "페이지. (0부터 시작)", example = "0", required = true)
    val page: Int = 0,
    @field:Parameter(description = "검색개수")
    val size: Int?
)