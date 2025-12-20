package link.yologram.api.domain.search.service.board

import jakarta.annotation.PostConstruct
import link.yologram.api.config.OpensearchConfig
import link.yologram.api.domain.bms.repository.board.BoardRepository
import link.yologram.api.domain.search.document.BoardDocument
import link.yologram.api.domain.search.exception.BoardNotFoundException
import link.yologram.api.domain.search.model.BoardIndexingRequest
import link.yologram.api.global.extension.readFileAsString
import link.yologram.api.global.extension.toJsonExcludeNull
import link.yologram.api.domain.search.service.OpensearchService
import link.yologram.api.infra.sqs.SqsClient
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequest
import org.opensearch.action.bulk.BulkRequest
import org.opensearch.action.index.IndexRequest
import org.opensearch.common.xcontent.XContentType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class BoardIndexingService(
    private val osService: OpensearchService,
    private val boardRepository: BoardRepository,
    private val indexProperties: OpensearchConfig.BoardIndexProperties,
    @Value("\${sqs.search.indexing.board.queue-name}") private val queueName: String,
    private val sqsClient: SqsClient
) {
    private val logger = LoggerFactory.getLogger(BoardIndexingService::class.java)
    private val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")


    @PostConstruct
    fun init() {
        createTemplate()
        createIndexWithAlias()
    }


    fun index(bid: Long) {
        val board = boardRepository.findBoardWithMetricsById(bid)
            ?: throw BoardNotFoundException("Board not found")

        val boardDocument = BoardDocument.of(board)
        val jsonDoc = board.toJsonExcludeNull()
        val response = osService.indexDocument(
            index = indexProperties.indexAlias,
            json = jsonDoc,
            docId = "${boardDocument.id}"
        )
    }

    fun index(from: Long, to: Long) {
        var currentFrom = from
        try {
            while (currentFrom <= to) {
                // 현재 배치에서 처리할 끝 번호 계산 (to를 넘지 않도록 minOf 사용)
                val currentTo = minOf(currentFrom + BULK_INDEXING_REQUEST_BATCH_SIZE - 1, to)

                sqsClient.send(
                    queueName = queueName,
                    attributes = emptyMap(),
                    messageBody = BoardIndexingRequest(from = currentFrom, to = currentTo)
                )

                logger.info("### sqs message board indexing sent [range: $currentFrom - $currentTo]")
                // 다음 배치의 시작 번호로 이동
                currentFrom = currentTo + 1
            }
        } catch (e: Exception) {
            logger.error("### SQS Error during range [$currentFrom]: ${e.message}")
        }
    }


    fun fullIndex() {
        val maxBid = boardRepository.findMaxBid().orElse(0L)

        if (maxBid == 0L) {
            logger.info("### No boards found in database")
            return
        }

        logger.info("### full indexing requested. max board id: $maxBid")

        var currentFrom = 1L

        try {
            while (currentFrom <= maxBid) {
                // 현재 배치에서 처리할 끝 번호 계산 (maxBid를 넘지 않도록 minOf 사용)
                val currentTo = minOf(currentFrom + BULK_INDEXING_REQUEST_BATCH_SIZE - 1, maxBid)

                logger.info("### sqs message board indexing sent [range: $currentFrom - $currentTo]")

                sqsClient.send(
                    queueName = queueName,
                    attributes = emptyMap(),
                    messageBody = BoardIndexingRequest(from = currentFrom, to = currentTo)
                )

                // 다음 배치의 시작 번호로 이동
                currentFrom = currentTo + 1
            }
        } catch (e: Exception) {
            logger.error("### SQS Error during full indexing range [$currentFrom]: ${e.message}")
        }
    }

    fun rangeIndexing(from: Long, to: Long) {
        val boards = boardRepository.findBoardsWithMetrics(from, to)

        if (boards.isEmpty()) {
            logger.info("### no boards found in range [$from, $to]")
            return
        }

        var totalIndexed = 0

        boards.chunked(BULK_INDEXING_BATCH_SIZE.toInt()).forEach { chunkedBoards ->
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
            }
        }
        logger.info("### completed indexing boards from [$from] to [$to]. Total indexed: $totalIndexed")
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

    companion object {
        const val BULK_INDEXING_REQUEST_BATCH_SIZE = 20L
        const val BULK_INDEXING_BATCH_SIZE = 5L
    }
}