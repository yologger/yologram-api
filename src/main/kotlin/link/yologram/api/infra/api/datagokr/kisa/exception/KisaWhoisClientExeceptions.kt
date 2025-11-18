package link.yologram.api.infra.api.datagokr.kisa.exception

open class KisaWhoisClientException(message: String?): Exception(message)

class NetworkException(message: String?): KisaWhoisClientException(message)

class KisaWhoisIpInfoFailureException(message: String?): KisaWhoisClientException(message)

class KisaWhoisDomainInfoFailureException(message: String?): KisaWhoisClientException(message)



