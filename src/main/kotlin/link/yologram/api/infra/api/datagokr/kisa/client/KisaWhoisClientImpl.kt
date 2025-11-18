package link.yologram.api.infra.api.datagokr.kisa.client

import io.github.oshai.kotlinlogging.KotlinLogging
import link.yologram.api.global.extension.mapper
import link.yologram.api.infra.api.datagokr.DataGoKrProperties
import link.yologram.api.infra.api.datagokr.kisa.client.dto.KisaWhoisIpInfoResponse
import link.yologram.api.infra.api.datagokr.kisa.exception.KisaWhoisIpInfoFailureException
import link.yologram.api.infra.api.datagokr.kisa.exception.NetworkException
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

class KisaWhoisClientImpl(
    private val webClient: WebClient,
    private val dataGoKrProperties: DataGoKrProperties,
): KisaWhoisClient {
    private val logger = KotlinLogging.logger { }
    private val dataGoKrKisaWhoisPrefix = "/B551505/whois"

    override fun getIpInfo(ip: String) = runCatching {
        val responseString = webClient
            .get()
            .uri { uriBuilder ->
                uriBuilder.path("$dataGoKrKisaWhoisPrefix/ip_address")
                    .queryParam("serviceKey", dataGoKrProperties.serviceKey)
                    .queryParam("query", ip)
                    .queryParam("answer", "json")
                    .build()
            }
            .retrieve()
            .bodyToMono<String>()
            .block()!!
        mapper.readValue(responseString, KisaWhoisIpInfoResponse::class.java)
    }
        .recoverCatching { throw NetworkException(it.message) }
        .mapCatching {
            if (it.response.whois.error != null) throw KisaWhoisIpInfoFailureException(it.response.whois.error.errorMsg)
            else it.response
        }
}