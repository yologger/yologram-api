package link.yologram.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*
import kotlin.reflect.KClass

val mapper = jacksonObjectMapper()
    .apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }
    .apply { configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) }
    .apply { configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false) }
    .apply { configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false) }
    .apply { registerModule(JavaTimeModule()) }

infix fun <T : Any> String.deserialize(clazz: KClass<T>): T = mapper.readValue(this, clazz.java)

fun String.encodeBase64() = Base64.getEncoder().encodeToString(this.toByteArray())
fun String.decodeBase64() = String(Base64.getDecoder().decode(this))