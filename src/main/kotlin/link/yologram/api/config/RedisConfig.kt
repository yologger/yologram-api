package link.yologram.api.config

import io.lettuce.core.ReadFrom
import io.lettuce.core.SocketOptions
import io.lettuce.core.TimeoutOptions
import io.lettuce.core.cluster.ClusterClientOptions
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions
import io.lettuce.core.cluster.models.partitions.RedisClusterNode.NodeFlag
import io.lettuce.core.resource.Delay
import org.springframework.boot.autoconfigure.data.redis.ClientResourcesBuilderCustomizer
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.util.concurrent.TimeUnit


@Configuration
@EnableConfigurationProperties(RedisProperties::class)
class RedisConfig(private val properties: RedisProperties) {

    /**
     * [AWS Guide](https://docs.aws.amazon.com/AmazonElastiCache/latest/red-ug/BestPractices.Clients-lettuce.html)
     */
    @Bean
    fun clusterConfigurationBuilderCustomizer(): LettuceClientConfigurationBuilderCustomizer {
        return LettuceClientConfigurationBuilderCustomizer { builder ->
            if (properties.cluster != null) {
                val topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                    .enablePeriodicRefresh(Duration.ofSeconds(30L))
                    .enableAllAdaptiveRefreshTriggers()
                    .build()

                val clientOptions = ClusterClientOptions.builder()
                    .socketOptions(SocketOptions.builder().keepAlive(true).build())
                    .timeoutOptions(TimeoutOptions.enabled())
                    .validateClusterNodeMembership(false)
                    .nodeFilter {
                        !(it.`is`(NodeFlag.FAIL)
                                || it.`is`(NodeFlag.EVENTUAL_FAIL)
                                || it.`is`(NodeFlag.HANDSHAKE)
                                || it.`is`(NodeFlag.NOADDR))
                    }
                    .topologyRefreshOptions(topologyRefreshOptions)
                    .build()

                builder
                    .readFrom(ReadFrom.REPLICA_PREFERRED)
                    .clientOptions(clientOptions)
            }
        }
    }

    @Bean
    fun clientResourceBuilderCustomizer(): ClientResourcesBuilderCustomizer {
        return ClientResourcesBuilderCustomizer { builder ->
            builder.reconnectDelay {
                Delay.fullJitter(
                    Duration.ofMillis(100),     // minimum 100 millisecond delay
                    Duration.ofSeconds(3),   // maximum 3 second delay
                    100,                        // 100 millisecond base
                    TimeUnit.MILLISECONDS
                )
            }
        }
    }
}