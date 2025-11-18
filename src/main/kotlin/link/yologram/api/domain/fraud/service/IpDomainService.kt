package link.yologram.api.domain.fraud.service

import io.github.oshai.kotlinlogging.KotlinLogging
import link.yologram.api.domain.fraud.exception.FraudException
import link.yologram.api.domain.fraud.exception.KisaWhoisException
import link.yologram.api.domain.fraud.exception.NetworkException
import link.yologram.api.domain.fraud.model.IpInfoResponse
import link.yologram.api.global.model.APIEnvelop
import link.yologram.api.infra.api.datagokr.kisa.client.KisaWhoisClient
import link.yologram.api.infra.api.datagokr.kisa.exception.KisaWhoisIpInfoFailureException
import org.springframework.stereotype.Service

@Service
class IpDomainService(
    private val kisaWhoisClient: KisaWhoisClient
) {
    private val logger = KotlinLogging.logger {}

    fun getIpInfo(ip: String): APIEnvelop<IpInfoResponse> {
        return kisaWhoisClient.getIpInfo(ip).fold(
            onSuccess = { return APIEnvelop(data = IpInfoResponse.of(it.whois)) },
            onFailure = { exception ->
                when (exception) {
                    is KisaWhoisIpInfoFailureException -> throw KisaWhoisException(exception.message ?: "KISA Whois API error")
                    is NetworkException -> throw NetworkException(exception.message ?: "Network error occurred")
                    else -> throw FraudException(exception.message ?: "Unknown error occurred")
                }
            }
        )
    }
}
