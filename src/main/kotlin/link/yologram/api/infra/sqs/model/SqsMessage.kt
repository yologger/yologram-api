package link.yologram.api.infra.sqs.model

import java.time.OffsetDateTime

data class SqsMessage<DATA> (
    val eventType: String,
    /** 이벤트 발생시간 */
    val eventTime: OffsetDateTime = OffsetDateTime.now(),
    /** 이벤트 발생 User ID */
    val actorUid: Long,
    /** 이벤트 발생 Service */
    val source: String,
    /** 이벤트 Data */
    val data: DATA
)