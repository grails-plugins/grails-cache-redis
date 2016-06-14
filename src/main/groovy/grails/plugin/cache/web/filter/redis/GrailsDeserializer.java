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

import org.springframework.core.NestedIOException;
import org.springframework.core.serializer.Deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * @author Burt Beckwith
 */
public class GrailsDeserializer implements Deserializer<Object> {

    public Object deserialize(InputStream inputStream) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(inputStream) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
                try {
                    return Thread.currentThread().getContextClassLoader().loadClass(osc.getName());
                } catch (Exception e) {
                    return super.resolveClass(osc);
                }
            }
        };

        try {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new NestedIOException("Failed to deserialize object type", e);
        }
    }

}
