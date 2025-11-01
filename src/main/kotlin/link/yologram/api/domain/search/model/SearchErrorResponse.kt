package link.yologram.api.domain.search.model

import link.yologram.api.domain.search.exception.SearchErrorCode

data class SearchErrorResponse(
    val errorCode: SearchErrorCode,
    val errorMessage: String
)
