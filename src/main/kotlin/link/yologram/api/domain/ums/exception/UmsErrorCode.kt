package link.yologram.api.domain.ums.exception

import link.yologram.api.global.excpetion.ErrorCode

enum class UmsErrorCode: ErrorCode {
    DUPLICATE_USER,
    USER_NOT_FOUND,
    USER_ALREADY_DELETED,
    UNKNOWN_ERROR
}