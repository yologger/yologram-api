package link.yologram.api.domain.search.service

import link.yologram.api.confg.TestOpensearchConfig
import link.yologram.api.confg.TestOpensearchContainer
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest
import org.opensearch.client.RequestOptions
import org.opensearch.client.RestHighLevelClient
import org.opensearch.client.indices.CreateIndexRequest
import org.opensearch.client.indices.GetIndexRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.Test

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        TestOpensearchContainer::class,
        TestOpensearchConfig::class
    ]
)
class OpensearchClientTest {

    @Autowired
    private lateinit var restHighLevelClient: RestHighLevelClient

    protected val testIndex = "test-index"

    @Test
    fun `인덱스 생성 테스트`() {
        // Given & When
        restHighLevelClient.indices().create(CreateIndexRequest(testIndex), RequestOptions.DEFAULT)

        // Then
        val exists = restHighLevelClient.indices().exists(GetIndexRequest(testIndex), RequestOptions.DEFAULT)
        assertTrue(exists)

        // Cleanup
        restHighLevelClient.indices().delete(DeleteIndexRequest(testIndex), RequestOptions.DEFAULT)
    }

    @Test
    fun `인덱스 삭제 테스트`() {
        // Given
        restHighLevelClient.indices().create(CreateIndexRequest(testIndex), RequestOptions.DEFAULT)
        assertTrue(restHighLevelClient.indices().exists(GetIndexRequest(testIndex), RequestOptions.DEFAULT))

        // When
        restHighLevelClient.indices().delete(DeleteIndexRequest(testIndex), RequestOptions.DEFAULT)

        // Then
        val exists = restHighLevelClient.indices().exists(GetIndexRequest(testIndex), RequestOptions.DEFAULT)
        assertFalse(exists)
    }

    @Test
    fun `인덱스 생성 및 삭제 통합 테스트`() {
        // 생성
        restHighLevelClient.indices().create(CreateIndexRequest(testIndex), RequestOptions.DEFAULT)
        assertTrue(restHighLevelClient.indices().exists(GetIndexRequest(testIndex), RequestOptions.DEFAULT))

        // 삭제
        restHighLevelClient.indices().delete(DeleteIndexRequest(testIndex), RequestOptions.DEFAULT)
        assertFalse(restHighLevelClient.indices().exists(GetIndexRequest(testIndex), RequestOptions.DEFAULT))
    }
}