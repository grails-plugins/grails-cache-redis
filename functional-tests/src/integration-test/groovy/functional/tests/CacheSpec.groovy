package functional.tests

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.test.mixin.integration.Integration
import grails.transaction.*
import spock.lang.Ignore

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

    @Ignore
    void testCacheAndEvict() {
        given:
            RestBuilder restBuilder = new RestBuilder()
            RestResponse response

        when: "check that there are no log entries"
            response = restBuilder.get("${baseUrl}/test/logEntryCount")
        then:
            response.text == '0'

        when:
            response = restBuilder.get("${baseUrl}/test/mostRecentLogEntry")
        then:
            response.text == 'none'

        when: "get the index action which should trigger caching"
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

        when: "get the index action again, should be cached"
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

        when: "evict"
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

        when: "save the values to compare"
            id++
            dateCreated = response.json.dateCreated
        and: "get the index action again, should not be cached"
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

        when: "try again with UrlMappings"
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
}
