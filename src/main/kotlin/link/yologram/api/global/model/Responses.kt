package link.yologram.api.global.model

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE
import io.swagger.v3.oas.annotations.media.Schema

@JsonAutoDetect(fieldVisibility = ANY, isGetterVisibility = NONE)
data class APIEnvelop<T>(
    @Schema(description = "data", type = "object", nullable = false)
    val data: T
)

data class APIEnvelopList<T>(
    val data: List<T>,
)

data class APIEnvelopPage<T>(
    val data: List<T>,

    @Schema(example = "0", description = "page", type = "number", nullable = true)
    val page: Int?,

    @Schema(example = "20", description = "page size", type = "number", nullable = true)
    val size: Int?
)

data class APIEnvelopCursorPage<T>(
    val data: List<T>,

    @Schema(description = "prev cursor", type = "string", nullable = true)
    val prevCursor: String? = null,

    @Schema(description = "next cursor", type = "string", nullable = true)
    val nextCursor: String? = null
)

data class APIEnvelopNextCursorPage<T>(
    val data: List<T>,

    @Schema(description = "next cursor", type = "string")
    val nextCursor: String? = null
)