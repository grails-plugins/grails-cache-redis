package functional.tests

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.test.mixin.integration.Integration

@Integration
class TaglibCachingSpec extends GebSpec{
    RestBuilder restBuilder
    RestResponse response

    def setup() {
        restBuilder = new RestBuilder()
        response = restBuilder.get("${baseUrl}/taglib/clearBlocksCache")
        println response.text
        response = restBuilder.get("${baseUrl}/taglib/clearTemplatesCache")
        println response.text
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
