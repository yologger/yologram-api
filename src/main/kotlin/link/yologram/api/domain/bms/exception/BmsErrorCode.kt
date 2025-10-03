package link.yologram.api.domain.bms.exception

import link.yologram.api.global.excpetion.ErrorCode

enum class BmsErrorCode: ErrorCode {
    // Board
    USER_NOT_FOUND,
    BOARD_NOT_FOUND,
    BOARD_WRONG_WRITER,
    BOARD_INVALID_PAGINATION_CURSOR,

    // BoardComment
    COMMENT_NOT_FOUND,
    BOARD_COMMENT_COUNT_NOT_FOUND,
    BOARD_COMMENT_MISMATCH,

    // Like
    USER_ALREADY_LIKE_BOARD,
    USER_NOT_LIKE_BOARD
}