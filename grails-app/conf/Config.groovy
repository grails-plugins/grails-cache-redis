log4j = {
	error 'org.codehaus.groovy.grails',
	      'org.springframework',
	      'org.hibernate',
	      'net.sf.ehcache.hibernate'
}

// for tests
grails.cache.config = {
	cache {
		name 'fromConfigGroovy1'
	}
	cache {
		name 'fromConfigGroovy2'
	}
}
