/* Copyright 2012 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import grails.plugin.cache.web.filter.redis.GrailsDeserializer
import grails.plugin.cache.web.filter.redis.GrailsDeserializingConverter
import grails.plugin.cache.web.filter.redis.GrailsRedisSerializer
import grails.plugin.cache.web.filter.redis.GrailsSerializer
import grails.plugin.cache.web.filter.redis.GrailsSerializingConverter
import grails.plugin.cache.web.filter.redis.RedisPageFragmentCachingFilter

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.cache.DefaultRedisCachePrefix
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisShardInfo
import redis.clients.jedis.Protocol

class CacheRedisGrailsPlugin {

	private final Logger log = LoggerFactory.getLogger('grails.plugin.cache.CacheRedisGrailsPlugin')

	String version = '0.1-SNAPSHOT'
	String grailsVersion = '2.0 > *'
	def loadAfter = ['cache']
	def pluginExcludes = ['scripts/CreateCacheRedisTestApps.groovy']

	String title = 'Redis Cache Plugin'
	String author = 'Burt Beckwith'
	String authorEmail = 'beckwithb@vmware.com'
	String description = 'A Redis-based implementation of the Cache plugin'
	String documentation = 'http://grails.org/plugin/cache-redis'

	String license = 'APACHE'
	def organization = [name: 'SpringSource', url: 'http://www.springsource.org/']
	def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/GPCACHEREDIS']
	def scm = [url: 'https://github.com/grails-plugins/grails-cache-redis']

	def doWithSpring = {
		if (!isEnabled(application)) {
			log.warn 'Redis Cache plugin is disabled'
			return
		}

		def cacheConfig = application.config.grails.cache.redis
		int database = cacheConfig.database ?: 0
		boolean usePool = (cacheConfig.usePool instanceof Boolean) ? cacheConfig.usePool : true
		String hostName = cacheConfig.hostName ?: 'localhost'
		int port = cacheConfig.port ?: Protocol.DEFAULT_PORT
		int timeout = cacheConfig.timeout ?: Protocol.DEFAULT_TIMEOUT
		String password = cacheConfig.password ?: null

		jedisPoolConfig(JedisPoolConfig)

		jedisShardInfo(JedisShardInfo, hostName, port) {
			password = password
			timeout = timeout
		}

		jedisConnectionFactory(JedisConnectionFactory) {
			usePool = usePool
			database = database
			hostName = hostName
			port = port
			timeout = timeout
			password = password
			poolConfig = ref('jedisPoolConfig')
			shardInfo = ref('jedisShardInfo')
		}

		// TODO bean name
		grailsSerializer(GrailsSerializer)
		// TODO bean name
		grailsDeserializer(GrailsDeserializer)

		deserializingConverter(GrailsDeserializingConverter) {
			// TODO bean name
			deserializer = ref('grailsDeserializer')
		}

		serializingConverter(GrailsSerializingConverter) {
			// TODO bean name
			serializer = ref('grailsSerializer')
		}

		redisSerializer(GrailsRedisSerializer) {
			serializer = ref('serializingConverter')
			deserializer = ref('deserializingConverter')
		}

		redisTemplate(RedisTemplate) {
			connectionFactory = ref('jedisConnectionFactory')
			defaultSerializer = ref('redisSerializer')
		}

		String delimiter = cacheConfig.cachePrefixDelimiter ?: ':'
		redisCachePrefix(DefaultRedisCachePrefix, delimiter)

		cacheManager(RedisCacheManager, ref('redisTemplate')) {
			cachePrefix = ref('redisCachePrefix')
		}

		grailsCacheFilter(RedisPageFragmentCachingFilter) {
			cacheManager = ref('cacheManager')
			nativeCacheManager = ref('redisTemplate')
			// TODO this name might be brittle - perhaps do by type?
			cacheOperationSource = ref('org.springframework.cache.annotation.AnnotationCacheOperationSource#0')
			keyGenerator = ref('webCacheKeyGenerator')
			expressionEvaluator = ref('webExpressionEvaluator')
		}
	}

	private boolean isEnabled(GrailsApplication application) {
		// TODO
		true
	}
}
