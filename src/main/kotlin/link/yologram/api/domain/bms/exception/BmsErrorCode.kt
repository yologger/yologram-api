package link.yologram.api.domain.bms.exception

import link.yologram.api.global.excpetion.ErrorCode

enum class BmsErrorCode: ErrorCode {
    USER_NOT_FOUND,
    BOARD_NOT_FOUND,
    BOARD_WRONG_WRITER
}