package functional.tests.helpers

import io.micronaut.http.HttpResponse
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import org.junit.Before
import spock.lang.Shared
import spock.lang.Specification

abstract class HttpClientSpec extends Specification {

    @Shared
    BlockingHttpClient blockingClient

    void setup() {
        if (!blockingClient) {
            HttpClient client = HttpClient.create(new URL("http://localhost:${serverPort}"))
            blockingClient = client.toBlocking()
        }
    }

    def <T> HttpResponse<T> get(String uri, Class<T> type = String) {
        this.blockingClient.exchange(uri, type)
    }
    
}