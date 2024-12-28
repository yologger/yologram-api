package link.yologram.api.domain.auth

import link.yologram.api.domain.auth.dto.AuthData

fun isAuthTokenHeader(headerKey: String): Boolean {
    return authTokenHeaderKeys.firstOrNull { key ->
        key.equals(headerKey, ignoreCase = true)
    } != null
}

val authTokenHeaderKeys = setOf(
    AuthData.USER_KEY,      // X_YOLOGRAM_USER_AUTH_TOKEN
    AuthData.SERVICE_KEY    // X_YOLOGRAM_SERVICE_AUTH_TOKEN
)