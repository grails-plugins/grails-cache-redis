package functional.tests

import functional.tests.helpers.RedisContainerHelper
import grails.testing.mixin.integration.Integration
import org.grails.plugin.cache.GrailsCacheManager
import spock.lang.Specification

@Integration
class SimpleCacheIntegrationSpec extends Specification implements RedisContainerHelper {

    GrailsCacheManager grailsCacheManager

    void "get cache from cache manager"() {
        expect:
        grailsCacheManager.getCache('dummy')
    }

    void "put element directly into cache"() {
        given:
        def cache = grailsCacheManager.getCache('dummy')

        expect: 'the cache is empty'
        !cache.get('first')

        when:
        cache.put('first', '#1')

        then:
        cache.get('first', String) == '#1'
    }

}


