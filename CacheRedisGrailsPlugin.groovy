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
import grails.plugin.cache.redis.GrailsRedisCacheManager
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
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisShardInfo
import redis.clients.jedis.Protocol

class CacheRedisGrailsPlugin {

	private final Logger log = LoggerFactory.getLogger('grails.plugin.cache.CacheRedisGrailsPlugin')

	String version = '1.1.1-SNAPSHOT'
	String grailsVersion = '2.4 > *'
	def loadAfter = ['cache']
	def pluginExcludes = [
		'grails-app/conf/*CacheConfig.groovy',
		'scripts/CreateCacheRedisTestApps.groovy',
		'docs/**',
		'src/docs/**'
	]

	String title = 'Redis Cache Plugin'
	String author = 'Burt Beckwith'
	String authorEmail = 'beckwithb@vmware.com'
	String description = 'A Redis-based implementation of the Cache plugin'
	String documentation = 'http://grails-plugins.github.io/grails-cache-redis/'

	String license = 'APACHE'
	def organization = [name: 'Pivotal', url: 'http://www.gopivotal.com/oss']
	def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/GPCACHEREDIS']
	def scm = [url: 'https://github.com/grails-plugins/grails-cache-redis']

	def doWithSpring = {
		if (!isEnabled(application)) {
			log.warn 'Redis Cache plugin is disabled'
			return
		}

		def cacheConfig = application.config.grails.cache
		def redisCacheConfig = cacheConfig.redis
		int database = redisCacheConfig.database ?: 0
		boolean usePool = (redisCacheConfig.usePool instanceof Boolean) ? redisCacheConfig.usePool : true
		String hostName = redisCacheConfig.hostName ?: 'localhost'
		int port = redisCacheConfig.port ?: Protocol.DEFAULT_PORT
		int timeout = redisCacheConfig.timeout ?: Protocol.DEFAULT_TIMEOUT
		String password = redisCacheConfig.password ?: null
		Boolean isUsePrefix = (redisCacheConfig.usePrefix instanceof Boolean) ? redisCacheConfig.usePrefix : false
		String keySerializerBean = redisCacheConfig.keySerializer instanceof String ?
			redisCacheConfig.keySerializer : null
		String hashKeySerializerBean = redisCacheConfig.hashKeySerializer instanceof String ?
			redisCacheConfig.hashKeySerializer : null

		grailsCacheJedisPoolConfig(JedisPoolConfig)

		grailsCacheJedisShardInfo(JedisShardInfo, hostName, port) {
			password = password
			timeout = timeout
		}

		grailsCacheJedisConnectionFactory(JedisConnectionFactory) {
			usePool = usePool
			database = database
			hostName = hostName
			port = port
			timeout = timeout
			password = password
			poolConfig = ref('grailsCacheJedisPoolConfig')
			shardInfo = ref('grailsCacheJedisShardInfo')
		}

		grailsRedisCacheSerializer(GrailsSerializer)

		grailsRedisCacheDeserializer(GrailsDeserializer)

		grailsRedisCacheDeserializingConverter(GrailsDeserializingConverter) {
			deserializer = ref('grailsRedisCacheDeserializer')
		}

		grailsRedisCacheSerializingConverter(GrailsSerializingConverter) {
			serializer = ref('grailsRedisCacheSerializer')
		}

		grailsCacheRedisSerializer(GrailsRedisSerializer) {
			serializer = ref('grailsRedisCacheSerializingConverter')
			deserializer = ref('grailsRedisCacheDeserializingConverter')
		}

		grailsCacheRedisTemplate(RedisTemplate) {
			connectionFactory = ref('grailsCacheJedisConnectionFactory')
			defaultSerializer = ref('grailsCacheRedisSerializer')
			if (keySerializerBean)
				keySerializer = ref(keySerializerBean)
			if (hashKeySerializerBean)
				hashKeySerializer = ref(hashKeySerializerBean)
		}

		String delimiter = redisCacheConfig.cachePrefixDelimiter ?: ':'
		redisCachePrefix(DefaultRedisCachePrefix, delimiter)

		grailsCacheManager(GrailsRedisCacheManager, ref('grailsCacheRedisTemplate')) {
			cachePrefix = ref('redisCachePrefix')
			usePrefix = isUsePrefix
		}

		grailsCacheFilter(RedisPageFragmentCachingFilter) {
			cacheManager = ref('grailsCacheManager')
			nativeCacheManager = ref('grailsCacheRedisTemplate')
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
