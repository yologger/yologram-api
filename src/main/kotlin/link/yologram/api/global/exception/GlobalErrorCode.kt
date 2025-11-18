package link.yologram.api.global.exception

enum class GlobalErrorCode: ErrorCode {
    HTTP_REQUEST_METHOD_NOT_ALLOWED,

    NOT_FOUND,
    INTERNAL_SERVER_ERROR
}