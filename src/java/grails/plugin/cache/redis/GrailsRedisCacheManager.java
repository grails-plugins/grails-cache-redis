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
package grails.plugin.cache.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Based on org.springframework.data.redis.cache.RedisCacheManager which has all private fields.
 *
 * @author Costin Leau
 * @author Burt Beckwith
 */
public class GrailsRedisCacheManager implements CacheManager {

	// fast lookup by name map
	protected final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();
	protected final Collection<String> names = Collections.unmodifiableSet(caches.keySet());
	@SuppressWarnings("rawtypes")
	protected final RedisTemplate redisTemplate;
	protected boolean usePrefix;
	protected RedisCachePrefix cachePrefix = new DefaultRedisCachePrefix();

	public GrailsRedisCacheManager(@SuppressWarnings("rawtypes") RedisTemplate template) {
		redisTemplate = template;
	}

	@SuppressWarnings("unchecked")
	public Cache getCache(String name) {
		Cache c = caches.get(name);
		if (c == null) {
			c = new GrailsRedisCache(name, (usePrefix ? cachePrefix.prefix(name) : null), redisTemplate);
			caches.put(name, c);
		}

		return c;
	}

	public Collection<String> getCacheNames() {
		return names;
	}

	/**
	 * Sets the cache prefix.
	 *
	 * @param prefix the prefix
	 */
	public void setCachePrefix(RedisCachePrefix prefix) {
		cachePrefix = prefix;
	}
}
