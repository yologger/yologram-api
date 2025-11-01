package link.yologram.api.confg

import org.apache.http.HttpHost
import org.opensearch.client.RestClient
import org.opensearch.client.RestHighLevelClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@TestConfiguration("TestOpensearchContainer")
@Testcontainers
class TestOpensearchContainer {
    companion object {
        private val OPENSEARCH_IMAGE = DockerImageName
            .parse("opensearchproject/opensearch:2.15.0")
            .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch")

        val opensearchContainer: ElasticsearchContainer = ElasticsearchContainer(OPENSEARCH_IMAGE)
            .withExposedPorts(9200, 9300)
            .withEnv("discovery.type", "single-node")
            .withEnv("plugins.security.disabled", "true")
            .withEnv("OPENSEARCH_INITIAL_ADMIN_PASSWORD", "MyStrongPassword123!") // 초기 admin 패스워드 설정
            .withEnv("DISABLE_INSTALL_DEMO_CONFIG", "true") // 데모 설정 비활성화
            .withEnv("OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m")
            .withReuse(false)
            .apply {
                start()
                println("OpenSearch container started at: ${httpHostAddress}")
            }
    }
}