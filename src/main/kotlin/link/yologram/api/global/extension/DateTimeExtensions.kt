package link.yologram.api.global.extension

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

object Zone {
    val KST: ZoneId = ZoneId.of("Asia/Seoul")
    val UTC: ZoneId = ZoneId.of("UTC")
}

fun LocalDateTime.toKst(): ZonedDateTime = atZone(Zone.KST)
fun LocalDateTime.toUtc(): LocalDateTime = toUtc(ZoneId.systemDefault())

fun LocalDateTime.atKstOffset(): OffsetDateTime = this.atOffset(ZoneOffset.of("+9"))
fun LocalDateTime.atUtcOffset(): OffsetDateTime = this.atOffset(ZoneOffset.UTC)

fun OffsetDateTime.toKst(): LocalDateTime = atZoneSameInstant(Zone.KST).toLocalDateTime()
fun OffsetDateTime.toUtc(): LocalDateTime = atZoneSameInstant(Zone.UTC).toLocalDateTime()

fun LocalDateTime.toUtc(zone: ZoneId): LocalDateTime {
    return LocalDateTime.from(this)
        .atZone(zone)
        .withZoneSameInstant(Zone.UTC)
        .toLocalDateTime()
}

fun Long.toUtc(zone: ZoneId): LocalDateTime {
    return ZonedDateTime
        .ofInstant(Instant.ofEpochMilli(this), zone)
        .withZoneSameInstant(Zone.UTC)
        .toLocalDateTime()
}