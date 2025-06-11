package functional.tests

class CachingServiceController {

	def cachingService

	def cachingServiceInvocationCount() {
		render text: "Basic Caching Service Invocation Count Is $cachingService.invocationCounter"
	}

	def cachingService() {
		render text: "Value From Service Is '$cachingService.data'"
	}

	def cachePut(String key, String value) {
		render text: "Result: ${cachingService.getData(key, value)}"
	}

	def cacheGet(String key) {
		render text: "Result: ${cachingService.getData(key)}"
	}

	def clear() {
		cachingService.clear()
		render text: 'OK'
	}
}
