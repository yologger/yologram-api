package link.yologram.api.confg

import link.yologram.api.confg.TestOpensearchContainer.Companion.opensearchContainer
import org.apache.http.HttpHost
import org.opensearch.client.RestClient
import org.opensearch.client.RestHighLevelClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn

@TestConfiguration
class TestOpensearchConfig {

    @Bean
    @DependsOn("TestOpensearchContainer")
    fun restHighLevelClient(): RestHighLevelClient {
        val httpHost = HttpHost.create(opensearchContainer.httpHostAddress)
        return RestHighLevelClient(
            RestClient.builder(httpHost)
                .setRequestConfigCallback { requestConfigBuilder ->
                    requestConfigBuilder
                        .setConnectTimeout(5000)
                        .setSocketTimeout(60000)
                }
        )
    }
}