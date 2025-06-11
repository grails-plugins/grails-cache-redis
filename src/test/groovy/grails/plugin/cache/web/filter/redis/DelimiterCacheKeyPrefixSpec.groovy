package grails.plugin.cache.web.filter.redis

import grails.plugin.cache.redis.internal.DelimiterCacheKeyPrefix
import spock.lang.Specification

class DelimiterCacheKeyPrefixSpec extends Specification {

    void 'prefix is generated as expected depending on input'() {
        given:
        DelimiterCacheKeyPrefix cacheKeyPrefix = new DelimiterCacheKeyPrefix(delimiter, prefix)

        expect:
        cacheKeyPrefix.compute('book') == expectedPrefix

        where:
        delimiter | prefix   || expectedPrefix
        null      | null     || 'book::'
        '_'       | null     || 'book_'
        null      | 'cache_' || 'cache_book::'
        '_'       | 'cache_' || 'cache_book_'
    }

}
