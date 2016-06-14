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

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @author Burt Beckwith
 */
public class GrailsRedisSerializer implements RedisSerializer<Object> {

    public static final byte[] EMPTY_ARRAY = new byte[0];

    protected Converter<Object, byte[]> serializer;
    protected Converter<byte[], Object> deserializer;

    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            return deserializer.convert(bytes);
        } catch (Exception e) {
            throw new SerializationException("Cannot deserialize", e);
        }
    }

    public byte[] serialize(Object object) {
        if (object == null) {
            return EMPTY_ARRAY;
        }

        try {
            return serializer.convert(object);
        } catch (Exception e) {
            throw new SerializationException("Cannot serialize", e);
        }
    }

    /**
     * Dependency injection for the serializer.
     *
     * @param serializer
     */
    public void setSerializer(Converter<Object, byte[]> serializer) {
        this.serializer = serializer;
    }

    /**
     * Dependency injection for the deserializer.
     *
     * @param deserializer
     */
    public void setDeserializer(Converter<byte[], Object> deserializer) {
        this.deserializer = deserializer;
    }

}
