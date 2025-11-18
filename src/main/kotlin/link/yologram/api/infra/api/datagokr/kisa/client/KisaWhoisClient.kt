package link.yologram.api.infra.api.datagokr.kisa.client

import link.yologram.api.infra.api.datagokr.kisa.client.dto.KisaWhoisIpInfoResponse

interface KisaWhoisClient {
    fun getIpInfo(ip: String): Result<KisaWhoisIpInfoResponse.Response>
}