/* Copyright 2014 the original author or authors
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

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @author <a href='mailto:donbeave@gmail.com'>Alexey Zhokhov</a>
 */
public class GrailsRedisKeySerializer implements RedisSerializer<Object> {

    protected GrailsRedisSerializer grailsCacheRedisSerializer;

    public GrailsRedisKeySerializer(GrailsRedisSerializer grailsCacheRedisSerializer) {
        this.grailsCacheRedisSerializer = grailsCacheRedisSerializer;
    }

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (o instanceof String) {
            return ((String) o).getBytes();
        }
        return grailsCacheRedisSerializer.serialize(o);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return grailsCacheRedisSerializer.deserialize(bytes);
    }

}
