package link.yologram.api.domain.auth.exception

import link.yologram.api.global.excpetion.ErrorCode

enum class AuthErrorCode: ErrorCode {
    USER_NOT_FOUND,
    WRONG_PASSWORD,
    INVALID_TOKEN_OWNER,
    UNKNOWN_ERROR,
    EXPIRED_TOKEN,
    INVALID_TOKEN,
    TOKEN_CREATION_FAIL,
}