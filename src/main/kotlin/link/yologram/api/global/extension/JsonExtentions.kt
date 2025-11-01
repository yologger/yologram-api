package link.yologram.api.global.extension

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDateTime
import java.time.ZonedDateTime
import kotlin.reflect.KClass

val mapper = jacksonObjectMapper()
    .apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }
    .apply { configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) }
    .apply { configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false) }
    .apply { configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false) }
    .apply { registerModule(JavaTimeModule()) }

infix fun <T : Any> String.deserialize(clazz: KClass<T>): T = mapper.readValue(this, clazz.java)

val excludeNullMapper: ObjectMapper = jacksonObjectMapper()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    .registerModule(JavaTimeModule())
    .registerModule(SimpleModule().addDeserializer(ZonedDateTime::class.java, ZonedDateTimeDeserializer()))
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)

class ZonedDateTimeDeserializer : JsonDeserializer<ZonedDateTime>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ZonedDateTime {
        return runCatching { ZonedDateTime.parse(p.text) }
            .getOrElse { LocalDateTime.parse(p.text).toKst() }
    }
}

fun <R> R.toJsonExcludeNull(): String {
    return excludeNullMapper.writeValueAsString(this)
}