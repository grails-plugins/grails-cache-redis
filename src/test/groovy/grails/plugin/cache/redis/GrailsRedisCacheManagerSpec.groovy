package grails.plugin.cache.redis

import grails.plugin.cache.redis.internal.DelimiterCacheKeyPrefix
import org.grails.testing.GrailsUnitTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializer
import spock.lang.Specification

class GrailsRedisCacheManagerSpec extends Specification implements GrailsUnitTest {

    void 'it should default expiration to never expire for all created caches when creating a cache manager with a null ttl value.'() {
        given:
        Long ttl = null
        RedisTemplate template = Stub(RedisTemplate)
        GrailsRedisCacheManager cacheManager = new GrailsRedisCacheManager(template)

        when:
        cacheManager.timeToLive = ttl
        and:
        GrailsRedisCache cache = cacheManager.getCache('book')

        then:
        cache.ttl == GrailsRedisCache.NEVER_EXPIRE
    }

    void 'it should set expiration on all caches with configured ttl when creating a cache manager.'() {
        given:
        Long ttl = 5
        RedisTemplate template = Stub(RedisTemplate)
        GrailsRedisCacheManager cacheManager = new GrailsRedisCacheManager(template)

        when:
        cacheManager.timeToLive = ttl
        and:
        GrailsRedisCache cache = cacheManager.getCache('book')

        then:
        cache.ttl == ttl
    }

    void 'it should generate a key with a prefix, if usePrefix is set'() {
        given:
        RedisTemplate template = Stub(RedisTemplate) {
            getKeySerializer() >> RedisSerializer.string()
        }
        GrailsRedisCacheManager cacheManager = new GrailsRedisCacheManager(template)

        when:
        cacheManager.usePrefix = usePrefix
        cacheManager.cachePrefix = cachePrefixGenerator
        and:
        GrailsRedisCache cache = cacheManager.getCache('book')
        and:
        String computedKey = new String(cache.computeKey('key'))

        then:
        computedKey == expectedKey

        where:
        usePrefix | delimiter | prefix   || expectedKey
        false     | null      | null     || 'key'
        true      | null      | null     || 'book::key'
        true      | '_'       | null     || 'book_key'
        true      | null      | 'cache_' || 'cache_book::key'
        true      | '_'       | 'cache_' || 'cache_book_key'

        cachePrefixGenerator = new DelimiterCacheKeyPrefix(delimiter, prefix)
    }

}
