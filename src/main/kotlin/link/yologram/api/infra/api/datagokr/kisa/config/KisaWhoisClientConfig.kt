package link.yologram.api.infra.api.datagokr.kisa.config

import link.yologram.api.infra.api.WebClientUtils
import link.yologram.api.infra.api.datagokr.DataGoKrProperties
import link.yologram.api.infra.api.datagokr.kisa.client.KisaWhoisClient
import link.yologram.api.infra.api.datagokr.kisa.client.KisaWhoisClientImpl
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@EnableConfigurationProperties(value = [DataGoKrProperties::class])
@Configuration
class KisaWhoisClientConfig {

    @Bean
    fun kisaWhoisClient(
        dataGoKrProperties: DataGoKrProperties,
    ): KisaWhoisClient {
        val webClient = WebClient
            .builder()
            .baseUrl(dataGoKrProperties.url)
            .clientConnector(
                WebClientUtils.createWebclientConnector(
                    connectionTimeoutMillis = dataGoKrProperties.connTimeout,
                    readTimeoutMillis = dataGoKrProperties.readTimeout,
                    writeTimeoutMillis = dataGoKrProperties.writeTimeout,
                    poolName = "datagokr-connection-pool",
                )
            )
            .build()

        return KisaWhoisClientImpl(webClient, dataGoKrProperties)
    }
}