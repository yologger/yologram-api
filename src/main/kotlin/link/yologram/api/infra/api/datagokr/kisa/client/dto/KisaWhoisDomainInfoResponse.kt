package link.yologram.api.infra.api.datagokr.kisa.client.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class KisaWhoisDomainInfoResponse(
    val response: Response,
) {
    data class Response(
        val result: ResultResponse,
        val whois: WhoisResponse,
    ) {
        data class WhoisResponse(
            @get:JsonProperty("krdomain")
            val krDomain: KrDomainWhoisResponse?,
            val error: ErrorResponse?,
        ) {
            data class KrDomainWhoisResponse(
                val name: String,
                val regName: String,
                val addr: String?,
                val post: String?,
                val adminName: String,
                val adminEmail: String,
                val adminPhone: String?,
                @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy. MM. dd.")
                val lastUpdatedDate: LocalDate,
                @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy. MM. dd.")
                val regDate: LocalDate,
                @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy. MM. dd.")
                val endDate: LocalDate,
                val infoYN: String,
                val domainStatus: List<String> = emptyList(),
                val agency: String,
                @get:JsonProperty("agency_url")
                val agencyUrl: String,
                val e_regName: String,
                val e_addr: String?,
                val e_adminName: String,
                val e_agency: String,
                val dnssec: String,
                val ns: List<String?> = emptyList(),
                val ip: List<String?> = emptyList(),
            )
        }
    }

    data class ResultResponse(
        @get:JsonProperty("result_code")
        val resultCode: String,
        @get:JsonProperty("result_msg")
        val resultMessage: String,
    )

    data class ErrorResponse(
        val query: String,
        @get:JsonProperty("error_code")
        val errorCode: String,
        @get:JsonProperty("error_msg")
        val errorMsg: String,
    )
}