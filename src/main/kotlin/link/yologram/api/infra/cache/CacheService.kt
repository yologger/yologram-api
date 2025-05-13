package link.yologram.api.infra.cache

interface CacheService {

    fun <V : Any> getOrNull(cache: Cache<V>): V?

    fun <V : Any> getAllOrNull(caches: List<Cache<V>>): List<V>?

    fun <V : Any> set(cache: Cache<V>, value: V)

    fun <V : Any> setAll(caches: Map<Cache<V>, V>)

    fun deleteAll(vararg caches: Cache<*>)
}