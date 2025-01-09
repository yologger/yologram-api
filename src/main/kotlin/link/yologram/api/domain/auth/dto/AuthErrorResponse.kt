package link.yologram.api.domain.auth.dto

import link.yologram.api.domain.auth.exception.AuthErrorCode

data class AuthErrorResponse(
    val errorMessage: String?,
    val errorCode: AuthErrorCode
)
