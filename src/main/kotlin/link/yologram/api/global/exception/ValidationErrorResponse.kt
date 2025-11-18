package link.yologram.api.global.exception


data class ValidationErrorResponse(
    val errorMessage: String?,
    val errorCode: ValidationErrorCode
)