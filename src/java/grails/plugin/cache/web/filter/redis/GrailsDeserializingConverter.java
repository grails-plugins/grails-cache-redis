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
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.support.SerializationFailedException;

import java.io.ByteArrayInputStream;

/**
 * @author Burt Beckwith
 */
public class GrailsDeserializingConverter implements Converter<byte[], Object> {

    protected Deserializer<Object> deserializer;

    public Object convert(byte[] source) {
        try {
            return deserializer.deserialize(new ByteArrayInputStream(source));
        } catch (Throwable t) {
            throw new SerializationFailedException("Failed to deserialize payload. " +
                    "Is the byte array a result of corresponding serialization for " +
                    deserializer.getClass().getSimpleName() + "?", t);
        }
    }

    /**
     * Dependency injection for the deserializer.
     *
     * @param deserializer
     */
    public void setDeserializer(Deserializer<Object> deserializer) {
        this.deserializer = deserializer;
    }

}
