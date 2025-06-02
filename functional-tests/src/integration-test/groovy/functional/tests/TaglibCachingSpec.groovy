package functional.tests

import functional.tests.helpers.HttpClientSpec
import functional.tests.helpers.RedisContainerHelper
import grails.testing.mixin.integration.Integration
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus

@Integration
class TaglibCachingSpec extends HttpClientSpec implements RedisContainerHelper {
    HttpResponse<?> response

    void setup() {
        response = get("/taglib/clearBlocksCache")
        println response.body()
        response = get("/taglib/clearTemplatesCache")
        println response.body()
    }

    void testBlockTag() {
        when:
        response = get("/taglib/blockCache?counter=5")
        then:
        response.status() == HttpStatus.OK
        response.body().contains('First block counter 6')
        response.body().contains('Second block counter 7')
        response.body().contains('Third block counter 8')

        when:
        response = get("/taglib/blockCache?counter=42")
        then:
        response.status() == HttpStatus.OK
        response.body().contains('First block counter 6')
        response.body().contains('Second block counter 7')
        response.body().contains('Third block counter 8')
    }

    void testClearingBlocksCache() {
        when:
        response = get("/taglib/clearBlocksCache")
        then:
        response.status() == HttpStatus.OK
        response.body().contains('cleared blocks cache')

        when:
        response = get("/taglib/blockCache?counter=100")
        then:
        response.status() == HttpStatus.OK
        response.body().contains('First block counter 101')
        response.body().contains('Second block counter 102')
        response.body().contains('Third block counter 103')

        when:
        response = get("/taglib/blockCache?counter=42")
        then:
        response.status() == HttpStatus.OK
        response.body().contains('First block counter 101')
        response.body().contains('Second block counter 102')
        response.body().contains('Third block counter 103')

        when:
        response = get("/taglib/clearBlocksCache")
        then:
        response.status() == HttpStatus.OK
        response.body().contains('cleared blocks cache')

        when:
        response = get("/taglib/blockCache?counter=50")
        then:
        response.status() == HttpStatus.OK
        response.body().contains('First block counter 51')
        response.body().contains('Second block counter 52')
        response.body().contains('Third block counter 53')

        when:
        response = get("/taglib/blockCache?counter=150")
        then:
        response.status() == HttpStatus.OK
        response.body().contains('First block counter 51')
        response.body().contains('Second block counter 52')
        response.body().contains('Third block counter 53')
    }

    void testRenderTag() {
        when:
        response = get("/taglib/clearTemplatesCache")
        then:
        response.status() == HttpStatus.OK
        response.body().contains('cleared templates cache')

        when:
        response = get("/taglib/renderTag?counter=1")
        then:
        response.status() == HttpStatus.OK

        response.body().contains('First invocation: Counter value: 1')
        response.body().contains('Second invocation: Counter value: 1')
        response.body().contains('Third invocation: Counter value: 3')
        response.body().contains('Fourth invocation: Counter value: 3')
        response.body().contains('Fifth invocation: Counter value: 1')

        when:
        response = get("/taglib/renderTag?counter=5")
        then:
        response.status() == HttpStatus.OK

        response.body().contains('First invocation: Counter value: 1')
        response.body().contains('Second invocation: Counter value: 1')
        response.body().contains('Third invocation: Counter value: 3')
        response.body().contains('Fourth invocation: Counter value: 3')
        response.body().contains('Fifth invocation: Counter value: 1')

        when:
        response = get("/taglib/clearTemplatesCache")
        then:
        response.status() == HttpStatus.OK
        response.body().contains('cleared templates cache')

        when:
        response = get("/taglib/renderTag?counter=5")
        then:
        response.status() == HttpStatus.OK

        response.body().contains('First invocation: Counter value: 5')
        response.body().contains('Second invocation: Counter value: 5')
        response.body().contains('Third invocation: Counter value: 7')
        response.body().contains('Fourth invocation: Counter value: 7')
        response.body().contains('Fifth invocation: Counter value: 5')

        when:
        response = get("/taglib/renderTag?counter=1")
        then:
        response.status() == HttpStatus.OK

        response.body().contains('First invocation: Counter value: 5')
        response.body().contains('Second invocation: Counter value: 5')
        response.body().contains('Third invocation: Counter value: 7')
        response.body().contains('Fourth invocation: Counter value: 7')
        response.body().contains('Fifth invocation: Counter value: 5')
    }
}
