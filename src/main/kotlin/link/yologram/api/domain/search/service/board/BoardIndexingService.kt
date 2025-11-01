package link.yologram.api.domain.search.service.board

import jakarta.annotation.PostConstruct
import link.yologram.api.config.OpensearchConfig
import link.yologram.api.domain.bms.repository.board.BoardRepository
import link.yologram.api.domain.search.document.BoardDocument
import link.yologram.api.domain.search.exception.BoardNotFoundException
import link.yologram.api.global.extension.readFileAsString
import link.yologram.api.global.extension.toJsonExcludeNull
import link.yologram.api.domain.search.service.OpensearchService
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequest
import org.opensearch.action.bulk.BulkRequest
import org.opensearch.action.index.IndexRequest
import org.opensearch.action.index.IndexResponse
import org.opensearch.common.xcontent.XContentType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class BoardIndexingService(
    private val osService: OpensearchService,
    private val boardRepository: BoardRepository,
    private val indexProperties: OpensearchConfig.BoardIndexProperties,
) {
    private val logger = LoggerFactory.getLogger(BoardIndexingService::class.java)
    private val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    @PostConstruct
    fun init() {
        createTemplate()
        createIndexWithAlias()
    }

    fun index(bid: Long): IndexResponse? {
        val board = boardRepository.findBoardWithMetricsById(bid)
            ?: throw BoardNotFoundException("Board not found")

        val boardDocument = BoardDocument.of(board)
        val date = boardDocument.createdDate.format(formatter)
        val jsonDoc = board.toJsonExcludeNull()
        val response = osService.indexDocument(
            index = indexProperties.indexAlias,
            json = jsonDoc,
            docId = "${boardDocument.id}"
        )
        logger.info("### Document indexed: [board ${boardDocument.id}]")
        return response
    }

    // from 부터 to 까지의 board 조회 후, osService.bulkInsert()
    fun index(from: Long, to: Long) {
        val boards = boardRepository.findBoardsWithMetrics(from, to)

        if (boards.isEmpty()) {
            logger.info("### No boards found in range [$from, $to]")
            return
        }

        var totalIndexed = 0

        boards.chunked(20).forEach { chunkedBoards ->
            val bulkRequest = BulkRequest().apply {
                chunkedBoards.forEach { board ->
                    val boardDocument = BoardDocument.of(board)
                    val jsonDoc = boardDocument.toJsonExcludeNull()
                    add(
                        IndexRequest(indexProperties.indexAlias)
                            .id(boardDocument.id.toString())
                            .source(jsonDoc, XContentType.JSON)
                    )
                }
            }

            val response = osService.bulkInsert(bulkRequest)

            if (response.hasFailures()) {
                logger.error("### Bulk indexing failed: ${response.buildFailureMessage()}")
            } else {
                totalIndexed += chunkedBoards.size
                logger.info("### Bulk indexed ${chunkedBoards.size} boards (Total: $totalIndexed/${boards.size})")
            }
        }

        logger.info("### Completed indexing boards from [$from] to [$to]. Total indexed: $totalIndexed")
    }

    fun fullIndexing() {
        logger.info("### Starting full indexing...")

        val maxBid = boardRepository.findMaxBid().orElse(0L)

        if (maxBid == 0L) {
            logger.info("### No boards found in database")
            return
        }

        logger.info("### Max board ID: $maxBid")

        var totalIndexed = 0
        var currentFrom = 1L
        val batchSize = 20L

        while (currentFrom <= maxBid) {
            val currentTo = minOf(currentFrom + batchSize - 1, maxBid)

            logger.info("### Processing range [$currentFrom - $currentTo]")

            val boards = boardRepository.findBoardsWithMetrics(currentFrom, currentTo)

            if (boards.isNotEmpty()) {
                val bulkRequest = BulkRequest().apply {
                    boards.forEach { board ->
                        val boardDocument = BoardDocument.of(board)
                        val jsonDoc = boardDocument.toJsonExcludeNull()
                        add(
                            IndexRequest(indexProperties.indexAlias)
                                .id(boardDocument.id.toString())
                                .source(jsonDoc, XContentType.JSON)
                        )
                    }
                }

                val response = osService.bulkInsert(bulkRequest)

                if (response.hasFailures()) {
                    logger.error("### Bulk indexing failed for range [$currentFrom - $currentTo]: ${response.buildFailureMessage()}")
                } else {
                    totalIndexed += boards.size
                    logger.info("### Successfully indexed ${boards.size} boards from range [$currentFrom - $currentTo] (Total: $totalIndexed)")
                }
            } else {
                logger.debug("### No boards found in range [$currentFrom - $currentTo]")
            }

            currentFrom = currentTo + 1
        }

        logger.info("### Full indexing completed. Total indexed: $totalIndexed boards")
    }

    private fun createTemplate() {
        logger.info("### Upserting template: [${indexProperties.templateName}]")
        val settingsJson = indexProperties.templateSettingsFile.readFileAsString()
        val mappingsJson = indexProperties.templateMappingsFile.readFileAsString()

        osService.putTemplate(
            templateName = indexProperties.templateName,
            settingsJson = settingsJson,
            mappingsJson = mappingsJson,
            patterns = listOf("${indexProperties.indexPrefix}-*"),
            shards = indexProperties.shards,
            replicas = indexProperties.replicas,
        )

        logger.info("### Upserted Template: [${indexProperties.templateName}]")
    }

    fun createIndexWithAlias() {
        with(indexProperties) {
            logger.info("### Upserting Index: [${indexProperties.indexName}]")
            osService.createIndex(indexName)    // 예: "board-index-v1" 생성
            logger.info("### Upserted Index: [${indexProperties.indexName}]")
            logger.info("### Upserting Alias: [${indexProperties.indexAlias}]")
            osService.updateAliases(
                IndicesAliasesRequest().apply {
                    addAliasAction(
                        IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                            .index(indexName)                   // "board-index-v1"
                            .alias(indexAlias)                  // "board-index"
                            .writeIndex(true)       // "board-index-v1"를 "board-index"의 writeIndex로 지정
                    )

                    preVersions.forEach { previousVersion ->    // 예: ["v0"]
                        "$indexPrefix-$previousVersion".takeIf { osService.existsIndex(it) }?.let { // "board-index-v0"이 존재하면
                            addAliasAction(
                                IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                                    .index(it)                  // "board-index-v0"
                                    .alias(indexAlias)                  // "board-index"
                                    .writeIndex(false)      // "board-index-v0"은 readOnlyIndex로 설정
                            )
                        }
                    }
                }
            )
            logger.info("### Upserted Alias: [${indexProperties.indexAlias}]")
        }
    }
}