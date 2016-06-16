package grails.plugin.cache.redis

import org.springframework.data.redis.cache.DefaultRedisCachePrefix
import org.springframework.data.redis.cache.RedisCachePrefix
import org.springframework.data.redis.core.RedisTemplate
import spock.lang.Specification

class GrailsRedisCacheSpec extends Specification {

    def 'it should default expiration to never expire when creating a cache with a null ttl value.'() {
        given:
            RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix()
            String cacheName = 'book'
            byte[] prefix = cachePrefix.prefix(cacheName)
            RedisTemplate template = Mock(RedisTemplate)
            Long ttl = null

        when:
            GrailsRedisCache cache = new GrailsRedisCache(cacheName, prefix, template, ttl)

        then:
            assert cache.ttl == GrailsRedisCache.NEVER_EXPIRE
    }

    def 'it should set expiration with configured ttl when creating a cache.'() {
        given:
            RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix()
            String cacheName = 'book'
            byte[] prefix = cachePrefix.prefix(cacheName)
            RedisTemplate template = Mock(RedisTemplate)
            Long ttl = 500

        when:
            GrailsRedisCache cache = new GrailsRedisCache(cacheName, prefix, template, ttl)

        then:
            assert cache.ttl == ttl
    }


}
