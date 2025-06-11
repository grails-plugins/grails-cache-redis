package functional.tests

import functional.tests.helpers.HttpClientSpec
import functional.tests.helpers.RedisContainerHelper
import grails.testing.mixin.integration.Integration
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import spock.lang.Specification

@Integration
class CachingServiceSpec extends HttpClientSpec implements RedisContainerHelper {


    HttpResponse<?> response

    void setup() {
        response = this.blockingClient.exchange("/cachingService/clear")
    }

    void testBasicCachingService() {
        when:
        response = get("/cachingService/cachingServiceInvocationCount")
        
        then:
        response.status() == HttpStatus.OK
        response.body().contains('Basic Caching Service Invocation Count Is 0')

        when:
        response = get('/cachingService/cachingService')
        then:
        response.status() == HttpStatus.OK
        response.body().contains("Value From Service Is 'Hello World!'")

        when:
        response = get('/cachingService/cachingServiceInvocationCount')
        then:
        response.status() == HttpStatus.OK
        response.body().contains("Basic Caching Service Invocation Count Is 1")

        when:
        response = get('/cachingService/cachingService')
        then:
        response.status() == HttpStatus.OK
        response.body().contains("Value From Service Is 'Hello World!'")

        when:
        response = get('/cachingService/cachingServiceInvocationCount')
        then:
        response.status() == HttpStatus.OK
        response.body().contains("Basic Caching Service Invocation Count Is 1")
    }


    void testBasicCachePutService() {

        when:
        response = get('/cachingService/cacheGet?key=band')
        then:
        response.status() == HttpStatus.OK
        response.body() == 'Result: null'

        when:
        response = get('/cachingService/cachePut?key=band&value=Thin+Lizzy')
        then:
        response.status() == HttpStatus.OK
        response.body() == 'Result: ** Thin Lizzy **'

        when:
        response = get('/cachingService/cacheGet?key=band')
        then:
        response.status() == HttpStatus.OK
        response.body() == 'Result: ** Thin Lizzy **'

        when:
        response = get('/cachingService/cacheGet?key=singer')
        then:
        response.status() == HttpStatus.OK
        response.body() == 'Result: null'

        when:
        response = get('/cachingService/cachePut?key=singer&value=Phil+Lynott')
        then:
        response.status() == HttpStatus.OK
        response.body() == 'Result: ** Phil Lynott **'

        when:
        response = get('/cachingService/cacheGet?key=singer')
        then:
        response.status() == HttpStatus.OK
        response.body() == 'Result: ** Phil Lynott **'

        when:
        response = get('/cachingService/cachePut?key=singer&value=John+Sykes')
        then:
        response.status() == HttpStatus.OK
        response.body() == 'Result: ** John Sykes **'

        when:
        response = get('/cachingService/cacheGet?key=singer')
        then:
        response.status() == HttpStatus.OK
        response.body() == 'Result: ** John Sykes **'

        when:
        response = get('/cachingService/cacheGet?key=band')
        then:
        response.status() == HttpStatus.OK
        response.body() == 'Result: ** Thin Lizzy **'
    }
    
}