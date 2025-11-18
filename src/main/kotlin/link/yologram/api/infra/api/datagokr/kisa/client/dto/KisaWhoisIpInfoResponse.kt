package link.yologram.api.infra.api.datagokr.kisa.client.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KisaWhoisIpInfoResponse(
    val response: Response,
) {
    data class Response(
        val result: ResultResponse,
        val whois: WhoisResponse,
    ) {
        data class ResultResponse(
            @get:JsonProperty("result_code")
            val resultCode: String,
            @get:JsonProperty("result_msg")
            val resultMessage: String,
        )

        data class WhoisResponse(
            val query: String?,
            val queryType: String?,
            val registry: String?,
            val countryCode: String?,
            val korean: IpResponse?,
            val english: IpResponse?,
            val error: ErrorResponse?,
        ) {
            data class IpResponse(
                val PI: IpDetailResponse?,
                val user: IpDetailResponse?
            ) {
                data class IpDetailResponse(
                    val netinfo: NetInfo?,
                    val techContact: TechContact?,
                ) {
                    data class NetInfo(
                        val range: String?,
                        val prefix: String?,
                        val netType: String?,
                        val servName: String?,
                        val orgName: String?,
                        val orgID: String?,
                        val addr: String?,
                        val zipCode: String?,
                        val regDate: String?,
                    )

                    data class TechContact(
                        val name: String?,
                        val phone: String?,
                        val email: String?,
                    )
                }
            }

            data class ErrorResponse(
                val query: String,
                @get:JsonProperty("error_code")
                val errorCode: String,
                @get:JsonProperty("error_msg")
                val errorMsg: String,
            )
        }
    }
}