package link.yologram.api.domain.ums.exception

import link.yologram.api.global.exception.ErrorCode

enum class UmsErrorCode: ErrorCode {

    /** AUTH **/
    AUTH_WRONG_PASSWORD,
    AUTH_INVALID_TOKEN_OWNER,
    AUTH_EXPIRED_TOKEN,
    AUTH_INVALID_TOKEN,
    AUTH_TOKEN_CREATION_FAILURE,
    AUTH_HEADER_EMPTY,

    /** USER **/
    USER_DUPLICATE,
    USER_NOT_FOUND,
    USER_ALREADY_DELETED,

    /** UMS **/
    UMS_UNKNOWN_ERROR
}