package link.yologram.api.domain.search.service.board

import com.fasterxml.jackson.databind.ObjectMapper
import link.yologram.api.config.OpensearchConfig
import link.yologram.api.domain.search.document.BoardDocument
import link.yologram.api.domain.search.model.BoardSearchResponse
import link.yologram.api.domain.search.service.OpensearchService
import org.opensearch.action.search.SearchRequest
import org.opensearch.index.query.QueryBuilders
import org.opensearch.search.builder.SearchSourceBuilder
import org.opensearch.search.sort.SortOrder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BoardSearchService(
    private val osService: OpensearchService,
    private val indexProperties: OpensearchConfig.BoardIndexProperties,
    private val mapper: ObjectMapper,
) {

    private val logger = LoggerFactory.getLogger(BoardSearchService::class.java)

    fun search(
        keyword: String?,
        page: Int,
        size: Int
    ): BoardSearchResponse {

        val searchSourceBuilder = SearchSourceBuilder().apply {
            // 검색 쿼리 설정
            query(
                if (keyword.isNullOrBlank()) {
                    // 키워드가 없으면 전체 조회
                    QueryBuilders.matchAllQuery()
                } else {
                    // 키워드가 있으면 title과 content에서 검색
                    QueryBuilders.multiMatchQuery(keyword)
                        .field("title", 2.0f)  // title에 가중치 2배
                        .field("content", 1.0f)
                }
            )

            // 페이지네이션
            from(page * size)
            size(size)

            // 정렬 (최신순)
            sort("id", SortOrder.DESC)

            // 응답에 포함할 필드 지정 (선택사항)
            fetchSource(true)
        }

        val searchRequest = SearchRequest(indexProperties.indexAlias)
            .source(searchSourceBuilder)

        logger.info("### Searching boards with keyword: [$keyword], page: $page, size: $size")

        val searchResponse = osService.search(searchRequest)

        val boards = searchResponse.hits.hits
            .map { hit ->
                mapper.readValue(hit.sourceAsString, BoardDocument::class.java)
            }

        val total = searchResponse.hits.totalHits?.value ?: 0L

        logger.info("### Found $total boards")

        return BoardSearchResponse(
            boards = boards,
            total = total,
            page = page,
            size = size
        )
    }
}