package grails.plugin.cache.redis

import org.springframework.data.redis.core.RedisTemplate
import spock.lang.Specification

class GrailsRedisCacheSpec extends Specification {

    void 'it should default expiration to never expire when creating a cache with a null ttl value.'() {
        given:
        String cacheName = 'book'
        RedisTemplate template = Mock(RedisTemplate)
        Long ttl = null

        when:
        GrailsRedisCache cache = new GrailsRedisCache(cacheName, null, template, ttl)

        then:
        cache.ttl == GrailsRedisCache.NEVER_EXPIRE
    }

    void 'it should set expiration with configured ttl when creating a cache.'() {
        given:
        String cacheName = 'book'
        RedisTemplate template = Mock(RedisTemplate)
        Long ttl = 500

        when:
        GrailsRedisCache cache = new GrailsRedisCache(cacheName, null, template, ttl)

        then:
        cache.ttl == ttl
    }
    
}
