package grails.plugin.cache.redis

import grails.config.Config
import grails.plugin.cache.web.filter.redis.*
import grails.plugins.Plugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.cache.CacheKeyPrefix
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Protocol

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class CacheRedisGrailsPlugin extends Plugin {

    private final Logger log = LoggerFactory.getLogger('grails.plugin.cache.CacheRedisGrailsPlugin')

    def grailsVersion = "6.2.0 > *"
    def loadAfter = ['cache']
    def pluginExcludes = [
            'grails-app/conf/*CacheConfig.groovy',
            'scripts/CreateCacheRedisTestApps.groovy',
            'docs/**',
            'src/docs/**'
    ]

    String title = 'Redis Cache Plugin'
    String description = 'A Redis-based implementation of the Cache plugin'

    def author = "Burt Beckwith"
    def authorEmail = "burt@burtbeckwith.com"

    def profiles = ['web']

    String documentation = 'http://grails-plugins.github.io/grails-cache-redis/'
    String license = 'APACHE'
    def developers = [
            [name: "Burt Beckwith", email: 'burt@burtbeckwith.com'],
            [name: 'Costin Leau'],
            [name: 'Colin Harrington', email: 'colin.harrington@gmail.com'],
            [name: 'Søren Berg Glasius', email: 'soeren@glasius.dk'],
    ]
    def issueManagement = [system: 'github', url: 'https://github.com/grails-plugins/grails-cache-redis/issues']
    def scm = [url: 'https://github.com/grails-plugins/grails-cache-redis']

    Closure doWithSpring() {
        { ->
            Config config = grailsApplication.config

            boolean pluginEnabled = config.getProperty('grails.cache.redis.enabled', Boolean, Boolean.TRUE)
            if (!pluginEnabled) {
                log.warn 'Redis Cache plugin is disabled'
                return
            }

            int configDatabase = config.getProperty('grails.cache.redis.database', Integer, 0)
            boolean configUsePool = config.getProperty('grails.cache.redis.usePool', Boolean, true)
            String configHostName = config.getProperty('grails.cache.redis.hostName', 'localhost')
            int configPort = config.getProperty('grails.cache.redis.port', Integer, Protocol.DEFAULT_PORT)
            int configTimeout = config.getProperty('grails.cache.redis.timeout', Integer, Protocol.DEFAULT_TIMEOUT)
            String configUsername = config.getProperty('grails.cache.redis.username')
            String configPassword = config.getProperty('grails.cache.redis.password')
            Long ttlInSeconds = config.getProperty('grails.cache.redis.ttl', Long, GrailsRedisCache.NEVER_EXPIRE)
            boolean isUsePrefix = config.getProperty('grails.cache.redis.usePrefix', Boolean, false)

            String keySerializerBean = config.getProperty('grails.cache.redis.keySerializer')
            String hashKeySerializerBean = config.getProperty('grails.cache.redis.hashKeySerializer')

            grailsCacheRedisPoolConfig(JedisPoolConfig)

            grailsCacheRedisConfiguration(RedisStandaloneConfiguration, configHostName, configPort) {
                username = configUsername
                if(configPassword) {
                    password = configPassword
                }
                database = configDatabase
            }

            grailsCacheJedisConnectionFactory(JedisConnectionFactory, ref('grailsCacheRedisConfiguration')) {
                usePool = configUsePool
                timeout = configTimeout
                poolConfig = ref('grailsCacheRedisPoolConfig')
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

            grailsCacheRedisKeySerializer(GrailsRedisKeySerializer, ref('grailsCacheRedisSerializer'))

            grailsCacheRedisTemplate(RedisTemplate) {
                connectionFactory = ref('grailsCacheJedisConnectionFactory')
                defaultSerializer = ref('grailsCacheRedisSerializer')
                if (keySerializerBean)
                    keySerializer = ref(keySerializerBean)
                if (hashKeySerializerBean)
                    hashKeySerializer = ref(hashKeySerializerBean)
            }

            String delimiter = config.getProperty('grails.cache.redis.cachePrefixDelimiter', CacheKeyPrefix.SEPARATOR)
            String prefix = config.getProperty('grails.cache.redis.cachePrefix', '')

            grailsRedisCachePrefix(DelimiterCacheKeyPrefix, delimiter, prefix)

            grailsCacheManager(GrailsRedisCacheManager, ref('grailsCacheRedisTemplate')) {
                cachePrefix = ref('grailsRedisCachePrefix', false)
                timeToLive = ttlInSeconds
                usePrefix = isUsePrefix
            }
        }
    }

}
