package link.yologram.api.global.excpetion

import link.yologram.api.global.excpetion.ValidationErrorCode

data class ValidationErrorResponse(
    val errorMessage: String,
    val errorCode: ValidationErrorCode
)