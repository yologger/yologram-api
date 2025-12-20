package link.yologram.api.infra.sqs

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import software.amazon.awssdk.services.sqs.SqsClient as AwsSqsClient

interface SqsClient {
    fun getQueueUrl(queueName: String): String

    fun <T> send(
        queueName: String,
        attributes: Map<String, String>,
        messageBody: T,
    )

    fun changeMessageVisibility(
        queueName: String,
        receiptHandle: String,
        visibilityTimeout: Int
    )
}

class SqsClientImpl(
    private val sqsClient: AwsSqsClient,
    private val objectMapper: ObjectMapper,
) : SqsClient, AutoCloseable {
    private val cachedQueue: ConcurrentMap<String, QueueUrl> = ConcurrentHashMap()
    private val logger = KotlinLogging.logger {}

    override fun getQueueUrl(queueName: String): String {
        return when {
            cacheHit(queueName) -> cachedQueue.getValue(queueName).value

            else -> GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build()
                .fetchQueueUrl()
                .also { cachedQueue[queueName] = QueueUrl(it) }
        }
    }

    override fun <T> send(
        queueName: String,
        attributes: Map<String, String>,
        messageBody: T,
    ) {
        SendMessageRequest.builder()
            .queueUrl(getQueueUrl(queueName))
            .messageAttributes(attributes.toMessageAttributes())
            .messageBody(objectMapper.writeValueAsString(messageBody))
            .build()
            .sendMessage()
    }

    override fun changeMessageVisibility(
        queueName: String,
        receiptHandle: String,
        visibilityTimeout: Int
    ) {
        val request = ChangeMessageVisibilityRequest.builder()
            .queueUrl(queueName)
            .receiptHandle(receiptHandle)
            .visibilityTimeout(visibilityTimeout)
            .build()

        sqsClient.changeMessageVisibility(request)
    }

    private fun cacheHit(queueName: String) = cachedQueue.containsKey(queueName)

    private fun GetQueueUrlRequest.fetchQueueUrl(): String {
        return runCatching { sqsClient.getQueueUrl(this).queueUrl()!! }
            .onFailure { logger.error(it) { "fail to get queue url" } }
            .getOrThrow()
    }

    private fun SendMessageRequest.sendMessage() {
        runCatching { sqsClient.sendMessage(this) }
            .onSuccess { logger.debug { "sqs message sent. result status code : ${it.sdkHttpResponse().statusCode()}" } }
            .onFailure { logger.error(it) { "fail to send sqs message" } }
            .getOrNull()
    }

    override fun close() {
        sqsClient.close()
        logger.info { "aws sqs client has closed." }
    }
}

@JvmInline
private value class QueueUrl(val value: String)

private fun Map<String, String>.toMessageAttributes() = mapValues {
    MessageAttributeValue.builder()
        .dataType("String")
        .stringValue(it.value)
        .build()
}
