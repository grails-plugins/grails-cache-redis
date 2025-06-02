package functional.tests


import functional.tests.helpers.RedisContainerHelper
import grails.plugin.cache.redis.GrailsRedisCacheManager
import grails.testing.mixin.integration.Integration
import org.grails.plugin.cache.GrailsCacheManager
import spock.lang.Specification

@Integration
class CachingServiceIntegrationSpec extends Specification implements RedisContainerHelper {

    GrailsCacheManager grailsCacheManager
    CachingService cachingService

    void cleanup() {
        cachingService.resetInvocationCounter()
    }
    
    void "type of cache manager"() {
        expect:
        grailsCacheManager instanceof GrailsRedisCacheManager
    }

    
    void "use a cacheable object"() {
        expect:
        cachingService.invocationCounter == 0

        when: 'accessing the method first time'
        String value = cachingService.data

        then: 'the expected value is returned and accessCount is incremented'
        value == 'Hello World!'
        cachingService.invocationCounter == 1

        when: 'accessing the method again'
        value = cachingService.data

        then: 'the expected value is returned, but accessCount is not incremented, thus using the cache'
        value == 'Hello World!'
        cachingService.invocationCounter == 1
    }


    void "use cacheable object with key"() {
        expect: 'That getting key-1 will yield null'
        !cachingService.getData('key-1') 

        when: 'putting a value into the cache'
        String value = cachingService.getData('key-1', 'value-1')

        then: 'the value is returned'
        value == '** value-1 **'

        when: 'getting the key'
        value = cachingService.getData('key-1')

        then: 'the value is still in the cache'
        value == '** value-1 **'
        
        when: 'clearing the cache'
        cachingService.clear()

        then: 'getting the key will yield null'
        !cachingService.getData('key-1') 
    }

}
