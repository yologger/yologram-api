package link.yologram.api.domain.fraud.model

import link.yologram.api.infra.api.datagokr.kisa.client.dto.KisaWhoisIpInfoResponse

data class IpInfoResponse(
    val query: String?,
    val queryType: String?,
    val registry: String?,
    val countryCode: String?,
    val range: String?,
    val prefix: String?,
    val netType: String?,
    val servName: String?,
    val orgName: String?,
    val orgID: String?,
    val addr: String?,
    val zipCode: String?,
    val regDate: String?,
    val name: String?,
    val phone: String?,
    val email: String?,
) {
    companion object {
        fun of(response: KisaWhoisIpInfoResponse.Response.WhoisResponse): IpInfoResponse {
            return IpInfoResponse(
                query = response.query,
                queryType = response.queryType,
                registry = response.registry,
                countryCode = response.countryCode,
                range = response.korean?.PI?.netinfo?.range ?: response.korean?.user?.netinfo?.range,
                prefix = response.korean?.PI?.netinfo?.prefix ?: response.korean?.user?.netinfo?.prefix,
                netType = response.korean?.PI?.netinfo?.netType ?: response.korean?.user?.netinfo?.netType,
                servName = response.korean?.PI?.netinfo?.servName ?: response.korean?.user?.netinfo?.servName,
                orgName = response.korean?.PI?.netinfo?.orgName ?: response.korean?.user?.netinfo?.orgName,
                orgID = response.korean?.PI?.netinfo?.orgID ?: response.korean?.user?.netinfo?.orgID,
                addr = response.korean?.PI?.netinfo?.addr ?: response.korean?.user?.netinfo?.addr,
                zipCode = response.korean?.PI?.netinfo?.zipCode ?: response.korean?.user?.netinfo?.zipCode,
                regDate = response.korean?.PI?.netinfo?.regDate ?: response.korean?.user?.netinfo?.regDate,
                name = response.korean?.PI?.techContact?.name ?: response.korean?.user?.techContact?.name,
                phone = response.korean?.PI?.techContact?.phone ?: response.korean?.user?.techContact?.phone,
                email = response.korean?.PI?.techContact?.email ?: response.korean?.user?.techContact?.email,
            )
        }
    }
}