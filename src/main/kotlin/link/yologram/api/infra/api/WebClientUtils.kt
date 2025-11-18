package link.yologram.api.infra.api

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration
import java.util.concurrent.TimeUnit

object WebClientUtils {
    fun createWebclientConnector(
        connectionTimeoutMillis: Int = 5000,
        readTimeoutMillis: Int = 5000,
        writeTimeoutMillis: Int = 5000,
        poolName: String = "custom-connection-pool",
        maxConnections: Int? = null,
    ): ClientHttpConnector {
        val connectionProvider = ConnectionProvider
            .builder(poolName)
            .apply { maxConnections?.let { this.maxConnections(it)} }
            .maxIdleTime(Duration.ofSeconds(10))
            .build()

        return HttpClient.create(connectionProvider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMillis)
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(readTimeoutMillis.toLong(), TimeUnit.MILLISECONDS))
                    .addHandlerLast(WriteTimeoutHandler(writeTimeoutMillis.toLong(), TimeUnit.MILLISECONDS))
            }.compress(true)
            .run {
                ReactorClientHttpConnector(this)
            }
    }
}