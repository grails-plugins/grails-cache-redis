package grails.plugin.cache.web.filter.redis;

import org.springframework.data.redis.cache.CacheKeyPrefix;

public class DelimiterCacheKeyPrefix implements CacheKeyPrefix {
    private final String delimiter;
    private final String prefix;
    
    public DelimiterCacheKeyPrefix(String delimiter, String prefix) {
        this.delimiter = delimiter;
        this.prefix = prefix;
    }

    @Override
    public String compute(String cacheName) {
        return (prefix == null ? "" : prefix)+cacheName+(delimiter == null ? CacheKeyPrefix.SEPARATOR : delimiter);
    }
    
}
