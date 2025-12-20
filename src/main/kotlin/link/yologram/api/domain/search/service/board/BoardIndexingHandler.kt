package link.yologram.api.domain.search.service.board

import com.fasterxml.jackson.core.JsonParseException
import io.awspring.cloud.sqs.annotation.SqsListener
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement
import link.yologram.api.domain.search.model.BoardIndexingRequest
import link.yologram.api.global.extension.deserialize
import link.yologram.api.infra.sqs.SqsClient
import org.springframework.beans.factory.annotation.Value
import software.amazon.awssdk.services.sqs.model.Message

@Component
class BoardIndexingHandler(
    @Value("\${sqs.search.indexing.board.queue-name}") private val queueName: String,
    private val sqsClient: SqsClient,
    private val boardIndexingService: BoardIndexingService
) {

    private val logger = KotlinLogging.logger {}

    /**
     * SQS 메시지 수신 리스너
     * - 비동기로 메시지를 처리. (SQS Listener 스레드 풀을 사용하여 별도 스레드에서 처리)
     * - MANUAL 모드: 처리 성공 후 수동으로 메시지 삭제
     */
    @SqsListener(
        value = ["\${sqs.search.indexing.board.queue-name}"],
        acknowledgementMode = "MANUAL",
        maxConcurrentMessages = "1",  // 동시에 1개만 처리 (순차 처리). handler thread count
        maxMessagesPerPoll = "1" // 한 번에 1개의 message만 가져옴

    )
    fun boardIndexingEventListener(message: Message, acknowledgment: Acknowledgement) {
        try {
            val request = message.body() deserialize BoardIndexingRequest::class
            processIndexingAsync(request, onSuccess = {
                // 처리 성공 시 메시지 삭제
                acknowledgment.acknowledge()
            })
        } catch (e: JsonParseException) {
            logger.error(e) { "Json parsing error in SQS listener :${e.message}. message: ${message.body()}" }
        } catch (e: Exception) {
            logger.error(e) { "consume error :${e.message}. message body : ${message.body()}" }
            sqsClient.changeMessageVisibility(
                queueName = queueName,
                receiptHandle = message.receiptHandle(),
                visibilityTimeout = 30
            )
        }
    }

    @Async("searchMigrationExecutor")
    fun processIndexingAsync(
        request: BoardIndexingRequest,
        onSuccess: () -> Unit
    ) {
        try {
            boardIndexingService.rangeIndexing(from = request.from, to = request.to)
            onSuccess()
        } catch (e: Exception) {
            logger.error(e) { "Failed to process SQS message: ${e.message}" }
            // 실패 시 메시지를 삭제하지 않음 (재시도 가능)
            // acknowledgment.acknowledge()를 호출하지 않음
        }
    }
}