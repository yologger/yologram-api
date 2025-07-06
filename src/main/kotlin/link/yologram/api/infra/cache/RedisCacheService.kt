package link.yologram.api.infra.cache

//import org.springframework.data.redis.core.StringRedisTemplate
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import io.github.oshai.kotlinlogging.KotlinLogging
//import org.springframework.stereotype.Service
//
//@Service
//class RedisCacheService(
//    private val redisTemplate: StringRedisTemplate,
//    private val objectMapper: ObjectMapper
//) : CacheService {
//
//    private val logger = KotlinLogging.logger {}
//
//    override fun <V : Any> getOrNull(cache: Cache<V>): V? =
//        runCatching {
//            val json = redisTemplate.opsForValue().get(cache.key)
//            if (json.isNullOrBlank()) null
//            else objectMapper.readValue(json, cache.type)
//        }.onFailure {
//            logger.error(it) { "unexpected error occurred while reading data from redis" }
//        }.getOrNull()
//
//    override fun <V : Any> getAllOrNull(caches: List<Cache<V>>): List<V>? =
//        caches.takeIf { it.isNotEmpty() }?.let { list ->
//            val keys = list.map { it.key }
//            val type = list.first().type
//            runCatching {
//                val jsons = redisTemplate.opsForValue().multiGet(keys)?.filterNotNull()
//                if (jsons.isNullOrEmpty()) null
//                else jsons.map { objectMapper.readValue(it, type) }
//            }.onFailure {
//                logger.error(it) { "unexpected error occurred while reading data from redis" }
//            }.getOrNull()
//        }
//
//    override fun <V : Any> set(cache: Cache<V>, value: V) {
//        runCatching {
//            val json = objectMapper.writeValueAsString(value)
//            redisTemplate.opsForValue()
//                .set(cache.key, json, cache.duration)
//        }.onFailure {
//            logger.error(it) { "unexpected error occurred while saving data to redis" }
//        }
//    }
//
//    override fun <V : Any> setAll(caches: Map<Cache<V>, V>) {
//        runCatching {
//            caches.forEach { (cache, value) ->
//                val json = objectMapper.writeValueAsString(value)
//                redisTemplate.opsForValue()
//                    .set(cache.key, json, cache.duration)
//            }
//        }.onFailure {
//            logger.error(it) { "unexpected error occurred while saving data to redis" }
//        }
//    }
//
//    override fun deleteAll(vararg caches: Cache<*>) {
//        runCatching {
//            val keys = caches.map { it.key }
//            redisTemplate.delete(keys)
//        }.onFailure {
//            logger.error(it) { "unexpected error occurred while deleting data from redis" }
//        }
//    }
//}