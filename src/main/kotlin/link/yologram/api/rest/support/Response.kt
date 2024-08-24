package link.yologram.api.rest.support

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Response<T>(val data: T) {
    companion object {
        fun <T> nullable(data: T?): Response<T?> = Response(data)
        fun <T> empty(): Response<T?> = Response(null)
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ListResponse<T>(val data: List<T>) {
    companion object
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TotalElementsResponse<T>(val data: List<T>, val totalElements: Long) {
    companion object
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PageResponse<T>(
    val data: List<T>,
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long
) {
    companion object
}