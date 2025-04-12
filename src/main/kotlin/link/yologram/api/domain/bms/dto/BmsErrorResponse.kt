package link.yologram.api.domain.bms.dto

import link.yologram.api.domain.bms.exception.BmsErrorCode

data class BmsErrorResponse(
    val errorCode: BmsErrorCode,
    val message: String
)
