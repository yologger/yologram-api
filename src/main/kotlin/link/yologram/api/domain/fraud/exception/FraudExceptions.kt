package link.yologram.api.domain.fraud.exception

open class FraudException(message: String): RuntimeException(message)

class NetworkException(message: String): FraudException(message)

class KisaWhoisException(message: String): FraudException(message)