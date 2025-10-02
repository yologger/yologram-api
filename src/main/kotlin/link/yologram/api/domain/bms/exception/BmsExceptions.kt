package link.yologram.api.domain.bms.exception

open class BmsException(message: String): Exception(message)

class BoardCommentCountException(message: String): BmsException(message)

class BoardCommentMismatchException(message: String): BmsException(message)

class BoardNotFoundException(message: String): BmsException(message)

class BoardWrongWriterException(message: String): BmsException(message)

class CommentNotFoundException(message: String): BmsException(message)

class InvalidPaginationCursorException(message: String): BmsException(message)

class UserAlreadyLikeBoardException(message: String): BmsException(message)

class UserNotFoundException(message: String): BmsException(message)

class UserNotLikeBoardException(message: String): BmsException(message)