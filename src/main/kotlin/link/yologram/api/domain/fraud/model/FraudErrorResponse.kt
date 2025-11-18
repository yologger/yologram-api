package link.yologram.api.domain.fraud.model

import link.yologram.api.domain.fraud.exception.FraudErrorCode

data class FraudErrorResponse(
    val errorCode: FraudErrorCode,
    val errorMessage: String
)
