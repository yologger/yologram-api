package link.yologram.api.global.excpetion


data class GlobalErrorResponse(
    val errorMessage: String,
    val errorCode: GlobalErrorCode
)