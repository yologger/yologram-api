package link.yologram.api.global.util

import java.util.Base64

object Base64Util {

    fun encode(id: Long): String {
        val bytes = id.toString().toByteArray(Charsets.UTF_8)
        return Base64.getUrlEncoder().encodeToString(bytes)
    }

    fun decode(cursor: String?): Long? {
        if (cursor.isNullOrBlank()) return null
        return try {
            val decoded = Base64.getUrlDecoder().decode(cursor)
            String(decoded, Charsets.UTF_8).toLongOrNull()
        } catch (e: Exception) {
            null
        }
    }
}