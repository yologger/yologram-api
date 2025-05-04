package link.yologram.api.domain.ums.model

import link.yologram.api.domain.ums.exception.UmsErrorCode

data class UmsErrorResponse(
    val errorMessage: String?,
    val errorCode: UmsErrorCode
)
