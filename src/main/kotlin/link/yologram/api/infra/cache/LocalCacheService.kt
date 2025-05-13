package link.yologram.api.infra.cache

import io.github.oshai.kotlinlogging.KotlinLogging

class LocalCacheService: CacheService {

    private val logger = KotlinLogging.logger {}

    private val store = HashMap<String, Any?>()

    override fun <V : Any> getOrNull(cache: Cache<V>): V? {
        return store[cache.key] as? V?
    }

    override fun <V : Any> getAllOrNull(caches: List<Cache<V>>): List<V>? {
        return caches.mapNotNull { cache -> store[cache.key] as? V? }
    }

    override fun <V : Any> set(cache: Cache<V>, value: V) {
        synchronized(store) {
            store[cache.key] = value
            logger.info { store }
        }
    }

    override fun <V : Any> setAll(caches: Map<Cache<V>, V>) {
        synchronized(store) {
            caches.map { (cache, value) -> store[cache.key] = value }
            logger.info { store }
        }
    }

    override fun deleteAll(vararg caches: Cache<*>) {
        synchronized(store) {
            caches.forEach { store -= it.key }
            logger.info { store }
        }
    }
}