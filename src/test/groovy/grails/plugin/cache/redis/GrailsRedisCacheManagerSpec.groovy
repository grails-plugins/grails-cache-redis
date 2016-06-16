package grails.plugin.cache.redis

import org.springframework.data.redis.core.RedisTemplate
import spock.lang.Specification

class GrailsRedisCacheManagerSpec extends Specification {

    def 'it should default expiration to never expire for all created caches when creating a cache manager with a null ttl value.'() {
        given:
            Long ttl = null
            RedisTemplate template = Mock(RedisTemplate)
            GrailsRedisCacheManager cacheManager = new GrailsRedisCacheManager(template)

        when:
                cacheManager.setTimeToLive(ttl)
        and:
                GrailsRedisCache cache = cacheManager.getCache('book')

        then:
            assert cache.ttl == GrailsRedisCache.NEVER_EXPIRE
    }

    def 'it should set expiration on all caches with configured ttl when creating a cache manager.'() {
        given:
            Long ttl = 5
            RedisTemplate template = Mock(RedisTemplate)
            GrailsRedisCacheManager cacheManager = new GrailsRedisCacheManager(template)

        when:
                cacheManager.setTimeToLive(ttl)
        and:
            GrailsRedisCache cache = cacheManager.getCache('book')

        then:
            assert cache.ttl == ttl
    }
}
