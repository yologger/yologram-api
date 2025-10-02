package link.yologram.api.global.excpetion

data class ValidationException (
    val errorMessage: String?,
    val errorCode: ValidationErrorCode
)