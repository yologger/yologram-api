package link.yologram.api.domain.search.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.opensearch.action.DocWriteResponse
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequest
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest
import org.opensearch.action.bulk.BulkRequest
import org.opensearch.action.bulk.BulkResponse
import org.opensearch.action.delete.DeleteRequest
import org.opensearch.action.get.GetRequest
import org.opensearch.action.index.IndexRequest
import org.opensearch.action.index.IndexResponse
import org.opensearch.action.search.MultiSearchRequest
import org.opensearch.action.search.MultiSearchResponse
import org.opensearch.action.search.SearchRequest
import org.opensearch.action.search.SearchResponse
import org.opensearch.client.RequestOptions
import org.opensearch.client.RestHighLevelClient
import org.opensearch.client.indices.CreateIndexRequest
import org.opensearch.client.indices.GetIndexRequest
import org.opensearch.client.indices.PutComposableIndexTemplateRequest
import org.opensearch.cluster.metadata.ComposableIndexTemplate
import org.opensearch.cluster.metadata.Template
import org.opensearch.common.compress.CompressedXContent
import org.opensearch.common.settings.Settings
import org.opensearch.common.xcontent.XContentType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class OpensearchService(
    private val osClient: RestHighLevelClient,
    private val mapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(OpensearchService::class.java)

    // 단건 document insert + update
    fun indexDocument(index: String, json: String, docId: String? = null): IndexResponse? {
        val request = IndexRequest(index).apply {
            if (!docId.isNullOrBlank()) id(docId)
        }
        request.source(json, XContentType.JSON)
        val response = osClient.index(request, RequestOptions.DEFAULT)

        when (response.result) {
            DocWriteResponse.Result.CREATED -> logger.info("## document created. (id=${response.id}, index=$index, version=${response.version})")
            DocWriteResponse.Result.UPDATED -> logger.info("## document updated. (id=${response.id}, index=$index, version=${response.version})")
            else -> logger.warn("## fail to upsert document. (id=${response.id}, index=$index, result=${response.result}, response=$response)")
        }

        return response
    }

    fun <T> searchDocument(request: GetRequest, clazz: Class<T>): T? {
        val response = osClient.get(request, RequestOptions.DEFAULT)
        return response.takeIf { it.isExists }?.let {
            mapper.readValue(it.sourceAsString, clazz)
        }
    }

    fun <T> searchDocuments(request: SearchRequest, clazz: Class<T>): List<T> {
        val response = osClient.search(request, RequestOptions.DEFAULT)
        return response.hits.hits
            .map { hit ->
                mapper.readValue(hit.sourceAsString, clazz)
            }.toList()
    }

    fun deleteDocument(index: String, docId: String) {
        val request = DeleteRequest(index, docId)
        val response = osClient.delete(request, RequestOptions.DEFAULT)

        when (response.result) {
            DocWriteResponse.Result.DELETED -> logger.debug("document deleted. (id=${response.id}), index=$index")
            DocWriteResponse.Result.NOT_FOUND -> logger.debug("document not found. (id=${response.id}), index=$index")
            else -> logger.warn("[Delete] Unexpected response. (pid=${response.id}, index=$index, result=${response.result}, response=$response)")
        }
    }

    /**
     * Bulk API
     * 여러 document를 한 번의 request로 처리
     * _id를 기준으로 upsert 방식으로 동작
     */
    fun bulkInsert(request: BulkRequest): BulkResponse {
        return osClient.bulk(request, RequestOptions.DEFAULT)
    }

    fun search(request: SearchRequest): SearchResponse
        = osClient.search(request, RequestOptions.DEFAULT)

    fun multiSearch(request: MultiSearchRequest): MultiSearchResponse
        = osClient.msearch(request, RequestOptions.DEFAULT)

    // Upsert Template
    fun putTemplate(
        templateName: String,
        settingsJson: String,
        mappingsJson: String,
        patterns: List<String>,
        shards: Int,
        replicas: Int
    ) {

        val template = Template(
            Settings.builder()
                .loadFromSource(settingsJson, XContentType.JSON)
                .put("index.number_of_shards", shards)
                .put("index.number_of_replicas", replicas)
                .build(),
            CompressedXContent(mappingsJson),
            null,
        )

        val indexTemplate = ComposableIndexTemplate(
            patterns, template,
            null, null, null, null
        )

        val request = PutComposableIndexTemplateRequest().name(templateName).apply {
            indexTemplate(indexTemplate)
        }

        osClient.indices().putIndexTemplate(request, RequestOptions.DEFAULT)
    }

    // 인덱스가 존재하지 않을 경우 생성한다.
    fun createIndex(vararg indices: String) {
        indices.forEach { index ->
            index.takeUnless { existsIndex(it) }?.let { osClient.indices().create(CreateIndexRequest(it), RequestOptions.DEFAULT) }
        }
    }

    fun deleteIndex(index: String) {
        index.takeIf { existsIndex(it) }?.let {
            osClient.indices().delete(DeleteIndexRequest(it), RequestOptions.DEFAULT)
        }
    }

    // 인덱스 존재 여부를 확인한다.
    fun existsIndex(index: String): Boolean {
        return osClient.indices().exists(GetIndexRequest(index), RequestOptions.DEFAULT)
    }

    fun updateAliases(request: IndicesAliasesRequest) {
        osClient.indices().updateAliases(request, RequestOptions.DEFAULT)
    }
}