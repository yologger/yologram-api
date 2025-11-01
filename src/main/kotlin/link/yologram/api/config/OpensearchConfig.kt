package link.yologram.api.config

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.opensearch.client.RestClient
import org.opensearch.client.RestHighLevelClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.PropertyMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableConfigurationProperties(
    OpensearchConfig.OpensearchRestClientProperties::class,
    OpensearchConfig.UserIndexProperties::class,
    OpensearchConfig.BoardIndexProperties::class
)
class OpensearchConfig {

    @Bean
    fun restHighLevelClient(properties: OpensearchRestClientProperties): RestHighLevelClient {
        val map = PropertyMapper.get()
        val builder = RestClient.builder(*properties.uris.map { HttpHost.create(it) }.toTypedArray())
            .setCompressionEnabled(true)
            .setHttpClientConfigCallback { httpClientBuilder ->
                httpClientBuilder.run {
                    if (!properties.username.isNullOrBlank() && !properties.password.isNullOrBlank()) {
                        setDefaultCredentialsProvider(
                            BasicCredentialsProvider().apply {
                                setCredentials(
                                    AuthScope.ANY,
                                    UsernamePasswordCredentials(properties.username, properties.password)
                                )
                            }
                        )
                    }
                    setMaxConnPerRoute(properties.maxConnPerRoute)
                    setMaxConnTotal(properties.maxConnTotal)
                }
            }
            .setRequestConfigCallback { requestConfigBuilder ->
                requestConfigBuilder.setContentCompressionEnabled(true)
                map.from { properties.connectionTimeout }.whenNonNull().asInt<Number>(Duration::toMillis)
                    .to { connectTimeout: Int? -> requestConfigBuilder.setConnectTimeout(connectTimeout!!) }
                map.from { properties.connectionRequestTimeout }.whenNonNull().asInt<Number>(Duration::toMillis)
                    .to { connectionRequestTimeout: Int? ->
                        requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout!!)
                    }
                map.from { properties.socketTimeout }.whenNonNull().asInt<Number>(Duration::toMillis)
                    .to { socketTimeout: Int? -> requestConfigBuilder.setSocketTimeout(socketTimeout!!) }

                requestConfigBuilder
            }
        return RestHighLevelClient(builder)
    }

    @ConfigurationProperties(prefix = "opensearch")
    data class OpensearchRestClientProperties(
        val enabled: Boolean = false,
        val uris: List<String>,
        val username: String? = null,
        val password: String? = null,
        val connectionTimeout: Duration = Duration.ofSeconds(1), // spring default is 1s
        val socketTimeout: Duration = Duration.ofSeconds(5), // spring default is 30s
        val connectionRequestTimeout: Duration = Duration.ofSeconds(1),
        val maxConnPerRoute: Int = 100, // spring default is 10
        val maxConnTotal: Int = 100, // spring default is 30
    )

    @ConfigurationProperties(prefix = "opensearch.index.user")
    data class UserIndexProperties(
        override val shards: Int = 1,
        override val replicas: Int = 1,
        override val indexPrefix: String = "user-index",
        override val templateSuffix: String = "template",
        override val templateSettingsFile: String = "template/user/user-settings.json",
        override val templateMappingsFile: String = "template/user/user-mappings.json",
        override val preVersions: List<String> = emptyList(),
        override val version: String = "v1",
    ): GeneralIndexProps {
        override val indexName: String     = "$indexPrefix-$version"
        override val indexPattern: String  = "$indexPrefix-*"
        override val indexAlias: String    = indexPrefix
        override val templateName: String  = "$indexPrefix-$templateSuffix"
    }

    @ConfigurationProperties(prefix = "opensearch.index.board")
    data class BoardIndexProperties(
        override val shards: Int = 1,
        override val replicas: Int = 1,
        override val indexPrefix: String = "board-index",
        override val templateSuffix: String = "template",
        override val templateSettingsFile: String = "template/board/board-settings.json",
        override val templateMappingsFile: String = "template/board/board-mappings.json",
        override val preVersions: List<String> = emptyList(),
        override val version: String = "v1",
    ): GeneralIndexProps {
        override val indexName: String     = "$indexPrefix-$version"
        override val indexPattern: String  = "$indexPrefix-*"
        override val indexAlias: String    = indexPrefix
        override val templateName: String  = "$indexPrefix-$templateSuffix"
    }

    interface GeneralIndexProps {
        val shards: Int
        val replicas: Int
        val indexPrefix: String
        val templateSuffix: String
        val preVersions: List<String>
        val version: String
        val templateSettingsFile: String
        val templateMappingsFile: String
        val indexName: String
        val indexPattern: String
        val indexAlias: String
        val templateName: String
    }
}