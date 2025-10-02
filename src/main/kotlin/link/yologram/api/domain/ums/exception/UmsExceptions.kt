package link.yologram.api.domain.ums.exception

open class UmsException(message: String): Exception(message)

class AuthHeaderEmptyException(message: String): UmsException(message)

class AuthInvalidTokenOwnerException(message: String): UmsException(message)

class AuthTokenCreationFailureException(message: String): UmsException(message)

class AuthTokenExpiredException(message: String): UmsException(message)

class AuthTokenInvalidException(message: String): UmsException(message)

class AuthWrongPasswordException(message: String): UmsException(message)

class UserAlreadyDeletedException(message: String): UmsException(message)

class UserDuplicateException(message: String): UmsException(message)

class UserNotFoundException(message: String): UmsException(message)