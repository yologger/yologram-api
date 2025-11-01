package link.yologram.api.domain.search.model

import io.swagger.v3.oas.annotations.Parameter

data class BoardFindParams(
    @field:Parameter(description = "검색어")
    val q: String? = null,
)