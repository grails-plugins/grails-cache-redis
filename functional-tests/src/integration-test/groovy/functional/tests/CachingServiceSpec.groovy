package functional.tests

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.test.mixin.integration.Integration

@Integration
class CachingServiceSpec extends GebSpec {

    RestBuilder restBuilder
    RestResponse response

    def setup() {
        restBuilder = new RestBuilder()
        response = restBuilder.get("${baseUrl}/cachingService/clear")
    }

    void testBasicCachingService() {
        when:
            response = restBuilder.get("${baseUrl}/cachingService/cachingServiceInvocationCount")
        then:
            response.status == 200
            response.text.contains('Basic Caching Service Invocation Count Is 0')

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cachingService")
        then:
            response.status == 200
            response.text.contains("Value From Service Is 'Hello World!'")

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cachingServiceInvocationCount")
        then:
            response.status == 200
            response.text.contains("Basic Caching Service Invocation Count Is 1")

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cachingService")
        then:
            response.status == 200
            response.text.contains("Value From Service Is 'Hello World!'")

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cachingServiceInvocationCount")
        then:
            response.status == 200
            response.text.contains("Basic Caching Service Invocation Count Is 1")
    }


    void testBasicCachePutService() {

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cacheGet?key=band")
        then:
            response.status == 200
            response.text == 'Result: null'

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cachePut?key=band&value=Thin Lizzy")
        then:
            response.status == 200
            response.text == 'Result: ** Thin Lizzy **'

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cacheGet?key=band")
        then:
            response.status == 200
            response.text == 'Result: ** Thin Lizzy **'

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cacheGet?key=singer")
        then:
            response.status == 200
            response.text == 'Result: null'

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cachePut?key=singer&value=Phil Lynott")
        then:
            response.status == 200
            response.text == 'Result: ** Phil Lynott **'

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cacheGet?key=singer")
        then:
            response.status == 200
            response.text == 'Result: ** Phil Lynott **'

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cachePut?key=singer&value=John Sykes")
        then:
            response.status == 200
            response.text == 'Result: ** John Sykes **'

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cacheGet?key=singer")
        then:
            response.status == 200
            response.text == 'Result: ** John Sykes **'

        when:
            response = restBuilder.get("${baseUrl}/cachingService/cacheGet?key=band")
        then:
            response.status == 200
            response.text == 'Result: ** Thin Lizzy **'
    }
}
