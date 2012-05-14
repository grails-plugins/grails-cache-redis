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
package grails.plugin.cache.web.filter.redis;

import grails.plugin.cache.web.PageInfo;
import grails.plugin.cache.web.filter.PageFragmentCachingFilter;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * Redis-based implementation of PageFragmentCachingFilter.
 *
 * @author Burt Beckwith
 */
public class RedisPageFragmentCachingFilter extends PageFragmentCachingFilter {

//	@Override
//	protected void replaceCacheWithDecoratedCache(Cache cache, BlockingCache blocking) {
//		getNativeCacheManager().replaceCacheWithDecoratedCache(
//				(Ehcache)cache.getNativeCache(), (Ehcache)blocking.getNativeCache());
//	}

//	@Override
//	protected BlockingCache createBlockingCache(Cache cache) {
//		return new EhcacheBlockingCache((Ehcache)cache.getNativeCache());
//	}

	@Override
	protected int getTimeToLive(ValueWrapper wrapper) {
		// ttl not supported
		return Integer.MAX_VALUE;
	}

	@Override
	protected RedisCacheManager getNativeCacheManager() {
		return (RedisCacheManager)super.getNativeCacheManager();
	}

	@Override
	protected void put(Cache cache, String key, PageInfo pageInfo, Integer timeToLiveSeconds) {
		// just store, ttl not supported
		cache.put(key, pageInfo);
	}
}
