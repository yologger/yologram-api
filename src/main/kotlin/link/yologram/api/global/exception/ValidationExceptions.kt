package link.yologram.api.global.exception

data class ValidationException (
    val errorMessage: String?,
    val errorCode: ValidationErrorCode
)