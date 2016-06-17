package functional.tests

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.test.mixin.integration.Integration
import grails.transaction.*

@Integration
@Rollback
class CacheSpec extends GebSpec {

    def setup() {
        RestBuilder restBuilder = new RestBuilder()
        RestResponse response = restBuilder.get("${baseUrl}/test/evict")
        response.text == 'evict'

        response = restBuilder.get("${baseUrl}/test/clearCache?cacheName=message")
        response.text == "cleared cache 'message'"

        response = restBuilder.get("${baseUrl}/test/clearLogEntries")
        response.text == 'deleted all LogEntry instances'
    }

    def cleanup() {
        RestBuilder restBuilder = new RestBuilder()
        RestResponse response = restBuilder.get("${baseUrl}/test/clearCache?cacheName=message")
        response.text == "cleared cache 'message'"

        response = restBuilder.get("${baseUrl}/test/clearLogEntries")
        response.text == 'deleted all LogEntry instances'
    }

    void testCacheAndEvict() {
        given:
            RestBuilder restBuilder = new RestBuilder()
            RestResponse response
            // check that there are no log entries
        when:
            response = restBuilder.get("${baseUrl}/test/logEntryCount")
        then:
            response.text == '0'

        when:
            response = restBuilder.get("${baseUrl}/test/mostRecentLogEntry")
        then:
            response.text == 'none'

            // get the index action which should trigger caching

        when:
            response = restBuilder.get("${baseUrl}/test/index")
        then:
            response.text == 'index'

        when:
            response = restBuilder.get("${baseUrl}/test/logEntryCount")
        then:
            response.text == '1'

        when:
            response = restBuilder.get("${baseUrl}/test/mostRecentLogEntry")

        then:
            response.json.message == 'Called index() action'

            long id = response.json.id
            long dateCreated = response.json.dateCreated

            // get the index action again, should be cached

        when:
            response = restBuilder.get("${baseUrl}/test/index")
        then:
            response.text == 'index'

        when:
            response = restBuilder.get("${baseUrl}/test/logEntryCount")
        then:
            response.text == '1'

        when:
            response = restBuilder.get("${baseUrl}/test/mostRecentLogEntry")

        then:
            response.json.message == 'Called index() action'
            response.json.id == id
            response.json.dateCreated == dateCreated

            // evict

        when:
            response = restBuilder.get("${baseUrl}/test/evict")
        then:
            response.text == 'evict'

        when:
            response = restBuilder.get("${baseUrl}/test/logEntryCount")
        then:
            response.text == '2'

        when:
            response = restBuilder.get("${baseUrl}/test/mostRecentLogEntry")
        then:
            response.json.message == 'Called evict() action'
            response.json.id == id + 1
            response.json.dateCreated > dateCreated

        when:
            // save the values to compare
            id++
            dateCreated = response.json.dateCreated

            // get the index action again, should not be cached

        and:
            response = restBuilder.get("${baseUrl}/test/index")
        then:
            response.text == 'index'

        when:
            response = restBuilder.get("${baseUrl}/test/logEntryCount")
        then:
            response.text == '3'

        when:
            response = restBuilder.get("${baseUrl}/test/mostRecentLogEntry")
        then:
            response.json.message == 'Called index() action'
            response.json.id == id + 1
            response.json.dateCreated > dateCreated
    }

    void testParams() {
        given:
            RestBuilder restBuilder = new RestBuilder()
            RestResponse response

        when:
            response = restBuilder.get("${baseUrl}/test/withParams?foo=baz&bar=123")
        then:
            response.text == 'withParams baz 123'

        when:
            response = restBuilder.get("${baseUrl}/test/withParams?foo=baz2&bar=1234")
        then:
            response.text == 'withParams baz2 1234'

        when:
            response = restBuilder.get("${baseUrl}/test/withParams?foo=baz&bar=123")
        then:
            response.text == 'withParams baz 123'

            // try again with UrlMappings

        when:
            response = restBuilder.get("${baseUrl}/withParams/baz/123")
        then:
            response.text == 'withParams baz 123'

        when:
            response = restBuilder.get("${baseUrl}/withParams/baz2/1234")
        then:
            response.text == 'withParams baz2 1234'

        when:
            response = restBuilder.get("${baseUrl}/withParams/baz/123")
        then:
            response.text == 'withParams baz 123'
    }

    void testBasicCachingService() {
        given:
            RestBuilder restBuilder = new RestBuilder()
            RestResponse response

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
        given:
            RestBuilder restBuilder = new RestBuilder()
            RestResponse response

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

    void testBlockTag() {
        given:
            RestBuilder restBuilder = new RestBuilder()
            RestResponse response

        when:
            response = restBuilder.get("${baseUrl}/taglib/blockCache?counter=5")
        then:
            response.status == 200
            response.text.contains('First block counter 6')
            response.text.contains('Second block counter 7')
            response.text.contains('Third block counter 8')

        when:
            response = restBuilder.get("${baseUrl}/taglib/blockCache?counter=42")
        then:
            response.status == 200
            response.text.contains('First block counter 6')
            response.text.contains('Second block counter 7')
            response.text.contains('Third block counter 8')
    }

    void testClearingBlocksCache() {
        given:
            RestBuilder restBuilder = new RestBuilder()
            RestResponse response
        when:
            response = restBuilder.get("${baseUrl}/taglib/clearBlocksCache")
        then:
            response.status == 200
            response.text.contains('cleared blocks cache')

        when:
            response = restBuilder.get("${baseUrl}/taglib/blockCache?counter=100")
        then:
            response.status == 200
            response.text.contains('First block counter 101')
            response.text.contains('Second block counter 102')
            response.text.contains('Third block counter 103')

        when:
            response = restBuilder.get("${baseUrl}/taglib/blockCache?counter=42")
        then:
            response.status == 200
            response.text.contains('First block counter 101')
            response.text.contains('Second block counter 102')
            response.text.contains('Third block counter 103')

        when:
            response = restBuilder.get("${baseUrl}/taglib/clearBlocksCache")
        then:
            response.status == 200
            response.text.contains('cleared blocks cache')

        when:
            response = restBuilder.get("${baseUrl}/taglib/blockCache?counter=50")
        then:
            response.status == 200
            response.text.contains('First block counter 51')
            response.text.contains('Second block counter 52')
            response.text.contains('Third block counter 53')

        when:
            response = restBuilder.get("${baseUrl}/taglib/blockCache?counter=150")
        then:
            response.status == 200
            response.text.contains('First block counter 51')
            response.text.contains('Second block counter 52')
            response.text.contains('Third block counter 53')
    }

    void testRenderTag() {
        given:
            RestBuilder restBuilder = new RestBuilder()
            RestResponse response
        when:
            response = restBuilder.get("${baseUrl}/taglib/clearTemplatesCache")
        then:
            response.status == 200
            response.text.contains('cleared templates cache')

        when:
            response = restBuilder.get("${baseUrl}/taglib/renderTag?counter=1")
        then:
            response.status == 200

            response.text.contains('First invocation: Counter value: 1')
            response.text.contains('Second invocation: Counter value: 1')
            response.text.contains('Third invocation: Counter value: 3')
            response.text.contains('Fourth invocation: Counter value: 3')
            response.text.contains('Fifth invocation: Counter value: 1')

        when:
            response = restBuilder.get("${baseUrl}/taglib/renderTag?counter=5")
        then:
            response.status == 200

            response.text.contains('First invocation: Counter value: 1')
            response.text.contains('Second invocation: Counter value: 1')
            response.text.contains('Third invocation: Counter value: 3')
            response.text.contains('Fourth invocation: Counter value: 3')
            response.text.contains('Fifth invocation: Counter value: 1')

        when:
            response = restBuilder.get("${baseUrl}/taglib/clearTemplatesCache")
        then:
            response.status == 200
            response.text.contains('cleared templates cache')

        when:
            response = restBuilder.get("${baseUrl}/taglib/renderTag?counter=5")
        then:
            response.status == 200

            response.text.contains('First invocation: Counter value: 5')
            response.text.contains('Second invocation: Counter value: 5')
            response.text.contains('Third invocation: Counter value: 7')
            response.text.contains('Fourth invocation: Counter value: 7')
            response.text.contains('Fifth invocation: Counter value: 5')

        when:
            response = restBuilder.get("${baseUrl}/taglib/renderTag?counter=1")
        then:
            response.status == 200

            response.text.contains('First invocation: Counter value: 5')
            response.text.contains('Second invocation: Counter value: 5')
            response.text.contains('Third invocation: Counter value: 7')
            response.text.contains('Fourth invocation: Counter value: 7')
            response.text.contains('Fifth invocation: Counter value: 5')
    }
}
