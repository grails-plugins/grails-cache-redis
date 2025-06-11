package functional.tests

import grails.plugin.cache.CacheEvict
import grails.plugin.cache.Cacheable

class TestController extends AbstractCacheController {

	LogEntryDataService logEntryDataService
	
	@Cacheable('message')
	def index() {
		logEntryDataService.save(new LogEntry(message: 'Called index() action'))
		render 'index'
	}

	@Cacheable('message')
	def withParams(String foo, Integer bar) {
		render "withParams $foo $bar"
	}

	@CacheEvict(value='message', allEntries=true)
	def evict() {
		logEntryDataService.save(new LogEntry(message: 'Called evict() action'))
		render 'evict'
	}
}
