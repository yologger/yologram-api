package link.yologram.api.domain.bms.dto

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class GetBoardsRequest(
    @Parameter
    @Schema(
        required = false,
        description = "Returns items following the given cursor. The key is board Id.",
        nullable = true
    )
    val nextCursor: String? = null,

    @Parameter
    @Schema(example = "20", description = "page size", type = "number")
    @get:Min(1)
    @get:Max(40)
    val size: Long = 20
)