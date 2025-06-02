package functional.tests.helpers

import groovy.transform.SelfType
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import spock.lang.Specification

@SelfType(Specification)
trait RedisContainerHelper {

    static {
        new GenericContainer(DockerImageName.parse("redis:8-alpine")).tap {
            addExposedPort(6379)
            start()
            System.setProperty("grails.cache.redis.host", host)
            System.setProperty("grails.cache.redis.port", getMappedPort(6379).toString())
        }
    }

}
