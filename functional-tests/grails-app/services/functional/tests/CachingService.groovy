package functional.tests


import grails.plugin.cache.CacheEvict
import grails.plugin.cache.CachePut
import grails.plugin.cache.Cacheable

class CachingService {

    private int invocationCounter = 0

    int getInvocationCounter() {
        invocationCounter
    }

    void resetInvocationCounter() {
        invocationCounter = 0
    }

    @Cacheable('basic')
    String getData() {
        ++invocationCounter
        'Hello World!'
    }

    @Cacheable(value = 'basic', key = { key })
    String getData(String key) {
        null
    }

    @CachePut(value = 'basic', key = { key })
    String getData(String key, String value) {
        "** ${value} **"
    }

    @CacheEvict(value = 'basic', allEntries = true)
    void clear() {}
    
}
