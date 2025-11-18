package link.yologram.api.global.exception


data class GlobalErrorResponse(
    val errorMessage: String,
    val errorCode: GlobalErrorCode
)