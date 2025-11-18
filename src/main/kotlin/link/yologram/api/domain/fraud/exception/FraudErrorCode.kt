package link.yologram.api.domain.fraud.exception

import link.yologram.api.global.exception.ErrorCode

enum class FraudErrorCode: ErrorCode {
    NETWORK_ERROR,
    HTTP_REQUEST_ARGUMENT_INVALID,
    INTERNAL_ERROR
}