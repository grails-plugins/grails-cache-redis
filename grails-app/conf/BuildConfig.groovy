grails.project.work.dir = 'target'
grails.project.docs.output.dir = 'docs/manual' // for gh-pages branch
grails.project.source.level = 1.7
grails.project.target.level = 1.7

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
    	mavenCentral()
	}

	dependencies {
		compile 'redis.clients:jedis:2.5.1'
    	compile 'org.springframework:spring-expression:4.0.5.RELEASE'
		compile 'org.springframework.data:spring-data-redis:1.3.0.RELEASE'
	}

	plugins {
		build(':release:3.0.1', ':rest-client-builder:2.0.1') {
			export = false
		}
		compile ':cache:1.1.6'
	}
}
