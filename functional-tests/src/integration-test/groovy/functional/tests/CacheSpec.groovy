package functional.tests

import functional.tests.helpers.HttpClientSpec
import functional.tests.helpers.RedisContainerHelper
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import io.micronaut.http.HttpResponse
import spock.lang.Ignore
import spock.lang.Specification

@Integration
@Rollback
class CacheSpec extends HttpClientSpec implements RedisContainerHelper  {

    HttpResponse<?> response

    void setup() {
        response = get("/test/evict")
        response.body() == 'evict'

        response = get("/test/clearCache?cacheName=message")
        response.body() == "cleared cache 'message'"

        response = get("/test/clearLogEntries")
        response.body() == 'deleted all LogEntry instances'
    }

    void cleanup() {
        response.body() == "cleared cache 'message'"

        response = get("/test/clearLogEntries")
        response.body() == 'deleted all LogEntry instances'
    }

    @Ignore
    void testCacheAndEvict() {
        when: "check that there are no log entries"
        response = get("/test/logEntryCount")
        then:
        response.body() == '0'

        when:
        response = get("/test/mostRecentLogEntry")
        then:
        response.body() == 'none'

        when: "get the index action which should trigger caching"
        response = get("/test/index")
        then:
        response.body() == 'index'

        when:
        response = get("/test/logEntryCount")
        then:
        response.body() == '1'

        when:
        response = get("/test/mostRecentLogEntry", Map)

        then:
        response.body().message == 'Called index() action'

        long id = response.body().id
        long dateCreated = response.body().dateCreated

        when: "get the index action again, should be cached"
        response = get("/test/index")
        then:
        response.body() == 'index'

        when:
        response = get("/test/logEntryCount")
        then:
        response.body() == '1'

        when:
        response = get("/test/mostRecentLogEntry", Map)

        then:
        response.body().message == 'Called index() action'
        response.body().id == id
        response.body().dateCreated == dateCreated

        when: "evict"
        response = get("/test/evict")
        then:
        response.body() == 'evict'

        when:
        response = get("/test/logEntryCount")
        then:
        response.body() == '2'

        when:
        response = get("/test/mostRecentLogEntry", Map)
        then:
        response.body().message == 'Called evict() action'
        response.body().id == id + 1
        response.body().dateCreated > dateCreated

        when: "save the values to compare"
        id++
        dateCreated = response.json.dateCreated
        and: "get the index action again, should not be cached"
        response = get("/test/index")
        then:
        response.body() == 'index'

        when:
        response = get("/test/logEntryCount")
        then:
        response.body() == '3'

        when:
        response = get("/test/mostRecentLogEntry", Map)
        then:
        response.body().message == 'Called index() action'
        response.body().id == id + 1
        response.body().dateCreated > dateCreated
    }

    void testParams() {
        when:
        response = get("/test/withParams?foo=baz&bar=123")
        then:
        response.body() == 'withParams baz 123'

        when:
        response = get("/test/withParams?foo=baz2&bar=1234")
        then:
        response.body() == 'withParams baz2 1234'

        when:
        response = get("/test/withParams?foo=baz&bar=123")
        then:
        response.body() == 'withParams baz 123'

        when: "try again with UrlMappings"
        response = get("/withParams/baz/123")
        then:
        response.body() == 'withParams baz 123'

        when:
        response = get("/withParams/baz2/1234")
        then:
        response.body() == 'withParams baz2 1234'

        when:
        response = get("/withParams/baz/123")
        then:
        response.body() == 'withParams baz 123'
    }
}
