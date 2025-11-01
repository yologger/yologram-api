package link.yologram.api.domain.search.service

import link.yologram.api.confg.TestOpensearchConfig
import link.yologram.api.confg.TestOpensearchContainer
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.opensearch.action.admin.indices.refresh.RefreshRequest
import org.opensearch.action.get.GetRequest
import org.opensearch.client.RequestOptions
import org.opensearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        TestOpensearchContainer::class,
        TestOpensearchConfig::class,
        OpensearchService::class,
        JacksonAutoConfiguration::class
    ]
)
class OpensearchServiceTest {

    @Autowired
    private lateinit var opensearchService: OpensearchService

    @Autowired
    private lateinit var restHighLevelClient: RestHighLevelClient

    private val testIndex = "test-index"

    @BeforeEach
    fun setUp() {
        // 테스트 인덱스 생성
        opensearchService.createIndex(testIndex)
    }

    @AfterEach
    fun tearDown() {
        // 테스트 인덱스 삭제
        opensearchService.deleteIndex(testIndex)
    }

    @Test
    fun `indexDocument - docId 없이 문서 생성`() {
        // Given
        val json = """
            {
                "title": "테스트 제목",
                "content": "테스트 내용",
                "author": "홍길동"
            }
        """.trimIndent()

        // When
        opensearchService.indexDocument(testIndex, json)

        // Then - 인덱스 새로고침 후 검색으로 확인
        restHighLevelClient.indices().refresh(
            RefreshRequest(testIndex),
            RequestOptions.DEFAULT
        )

        val searchRequest = org.opensearch.action.search.SearchRequest(testIndex)
        val searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)

        assertTrue(searchResponse.hits.totalHits?.value!! > 0)
        val hit = searchResponse.hits.hits[0]
        val source = hit.sourceAsMap
        assertEquals("테스트 제목", source["title"])
        assertEquals("테스트 내용", source["content"])
        assertEquals("홍길동", source["author"])
    }

    @Test
    fun `indexDocument - docId와 함께 문서 생성`() {
        // Given
        val docId = "test-doc-1"
        val json = """
            {
                "title": "특정 ID로 생성",
                "content": "ID가 지정된 문서",
                "count": 100
            }
        """.trimIndent()

        // When
        opensearchService.indexDocument(testIndex, json, docId)

        // Then - GetRequest로 특정 ID 문서 조회
        val getRequest = GetRequest(testIndex, docId)
        val getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT)

        assertTrue(getResponse.isExists)
        assertEquals(docId, getResponse.id)
        val source = getResponse.sourceAsMap
        assertEquals("특정 ID로 생성", source["title"])
        assertEquals("ID가 지정된 문서", source["content"])
        assertEquals(100, source["count"])
    }

    @Test
    fun `indexDocument - 동일한 docId로 문서 업데이트`() {
        // Given
        val docId = "update-test-doc"
        val originalJson = """
            {
                "title": "원본 제목",
                "content": "원본 내용",
                "version": 1
            }
        """.trimIndent()

        val updatedJson = """
            {
                "title": "수정된 제목",
                "content": "수정된 내용",
                "version": 2
            }
        """.trimIndent()

        // When - 문서 생성
        opensearchService.indexDocument(testIndex, originalJson, docId)

        // 같은 ID로 다시 인덱싱 (업데이트)
        opensearchService.indexDocument(testIndex, updatedJson, docId)

        // Then - 문서가 업데이트되었는지 확인
        val getRequest = GetRequest(testIndex, docId)
        val getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT)

        assertTrue(getResponse.isExists)
        val source = getResponse.sourceAsMap
        assertEquals("수정된 제목", source["title"])
        assertEquals("수정된 내용", source["content"])
        assertEquals(2, source["version"])
    }

    @Test
    fun `indexDocument - 여러 문서 생성`() {
        // Given
        val documents = listOf(
            "doc-1" to """{"title": "첫 번째", "order": 1}""",
            "doc-2" to """{"title": "두 번째", "order": 2}""",
            "doc-3" to """{"title": "세 번째", "order": 3}"""
        )

        // When
        documents.forEach { (docId, json) ->
            opensearchService.indexDocument(testIndex, json, docId)
        }

        // Then
        restHighLevelClient.indices().refresh(
            RefreshRequest(testIndex),
            RequestOptions.DEFAULT
        )

        documents.forEach { (docId, _) ->
            val getRequest = GetRequest(testIndex, docId)
            val getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT)
            assertTrue(getResponse.isExists)
        }
    }

    @Test
    fun `indexDocument - JSON 객체 복잡한 구조`() {
        // Given
        val complexJson = """
            {
                "title": "복잡한 문서",
                "metadata": {
                    "author": "홍길동",
                    "tags": ["테스트", "검색", "OpenSearch"],
                    "stats": {
                        "views": 100,
                        "likes": 50
                    }
                },
                "published": true
            }
        """.trimIndent()

        // When
        opensearchService.indexDocument(testIndex, complexJson, "complex-doc")

        // Then
        val getRequest = GetRequest(testIndex, "complex-doc")
        val getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT)

        assertTrue(getResponse.isExists)
        val source = getResponse.sourceAsMap
        assertEquals("복잡한 문서", source["title"])
        assertEquals(true, source["published"])

        @Suppress("UNCHECKED_CAST")
        val metadata = source["metadata"] as Map<String, Any>
        assertEquals("홍길동", metadata["author"])

        @Suppress("UNCHECKED_CAST")
        val tags = metadata["tags"] as List<String>
        assertEquals(3, tags.size)
        assertTrue(tags.contains("테스트"))

        @Suppress("UNCHECKED_CAST")
        val stats = metadata["stats"] as Map<String, Any>
        assertEquals(100, stats["views"])
        assertEquals(50, stats["likes"])
    }

    @Test
    fun `indexDocument - null docId는 자동 생성된 ID 사용`() {
        // Given
        val json = """{"title": "자동 ID 문서"}"""

        // When
        opensearchService.indexDocument(testIndex, json, null)

        // Then - 검색으로 문서 존재 확인
        restHighLevelClient.indices().refresh(
            RefreshRequest(testIndex),
            RequestOptions.DEFAULT
        )

        val searchRequest = org.opensearch.action.search.SearchRequest(testIndex)
        val searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)

        assertTrue(searchResponse.hits.totalHits?.value!! > 0)
        val hit = searchResponse.hits.hits[0]
        assertNotNull(hit.id) // ID가 자동 생성됨
        assertEquals("자동 ID 문서", hit.sourceAsMap["title"])
    }
}
