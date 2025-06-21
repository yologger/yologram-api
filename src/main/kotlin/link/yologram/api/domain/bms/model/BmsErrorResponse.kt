package link.yologram.api.domain.bms.model

import link.yologram.api.domain.bms.exception.BmsErrorCode

data class BmsErrorResponse(
    val errorCode: BmsErrorCode,
    val errorMessage: String
)
