package link.yologram.api.global.extension

import org.springframework.core.io.ClassPathResource
import java.util.Base64

fun String.encodeBase64() = Base64.getEncoder().encodeToString(this.toByteArray())
fun String.decodeBase64() = String(Base64.getDecoder().decode(this))

fun String.readFileAsString(): String {
    return ClassPathResource(this).inputStream.use { input ->
        input.bufferedReader().use { it.readText() }
    }
}