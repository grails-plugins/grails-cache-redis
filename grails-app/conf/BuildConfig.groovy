grails.project.work.dir = 'target'
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsRepo "http://grails.org/plugins"
//		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {
		compile 'redis.clients:jedis:2.0.0'
		compile 'org.springframework.data:spring-data-redis:1.0.0.RELEASE'
	}

	plugins {
		build(':release:2.0.0', ':rest-client-builder:1.0.2') {
			export = false
		}
		compile ':cache:1.0.0.M1'
	}
}

