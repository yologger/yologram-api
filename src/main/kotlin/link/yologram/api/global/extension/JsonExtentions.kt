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

val mapper: ObjectMapper = jacksonObjectMapper()
    .apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) } // 직렬화 시 값이 null인 필드는 JSON 결과에서 제외
    .apply { configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false) } // 직렬화 시 필드가 없는(빈) 객체를 만나도 오류를 발생시키지 않고 허용
    .apply { configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false) } // 날짜 및 시간 객체(예: Date, LocalDateTime)를 UNIX 타임스탬프(숫자) 대신 ISO-8601 형식의 문자열로 직렬화
    .apply { configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) } // 역직렬화 시 JSON에 있지만 Java/Kotlin 객체에 없는 필드가 있어도 오류를 발생시키지 않고 무시
    .apply { registerModule(JavaTimeModule()) } // Java 8의 새로운 날짜/시간 API (JSR 310, 예: LocalDateTime, ZonedDateTime)를 Jackson이 올바르게 처리할 수 있도록 모듈을 등록

infix fun <T : Any> String.deserialize(clazz: KClass<T>): T = mapper.readValue(this, clazz.java)

val excludeNullMapper: ObjectMapper = jacksonObjectMapper()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // JSON에 없는 필드를 무시합니다
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // 날짜를 문자열로 직렬화합니다
    .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE) // 역직렬화 시 JSON 문자열에 정의되지 않은 Enum 값을 만나면 오류 대신 Enum의 기본값을 사용하도록 설정
    .registerModule(JavaTimeModule()) // Java 8 시간 타입 처리를 등록
    .registerModule(SimpleModule().addDeserializer(ZonedDateTime::class.java, ZonedDateTimeDeserializer())) // ZonedDateTime 타입에 대해 사용자 정의 ZonedDateTimeDeserializer를 사용하여 역직렬화하도록 등록
    .setSerializationInclusion(JsonInclude.Include.NON_NULL) // 직렬화 시 값이 null인 필드를 JSON에서 제외

class ZonedDateTimeDeserializer : JsonDeserializer<ZonedDateTime>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ZonedDateTime {
        return runCatching { ZonedDateTime.parse(p.text) }
            .getOrElse { LocalDateTime.parse(p.text).toKst() }
    }
}

fun <R> R.toJsonExcludeNull(): String {
    return excludeNullMapper.writeValueAsString(this)
}