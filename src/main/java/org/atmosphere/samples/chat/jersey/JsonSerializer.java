package org.atmosphere.samples.chat.jersey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author smecsia
 */
@Component
public class JsonSerializer {
    final ObjectMapper defaultObjectMapper;

    public JsonSerializer() {
        defaultObjectMapper = createDefaultMapper();
    }

    private ObjectMapper createDefaultMapper() {
        final ObjectMapper result = new ObjectMapper();
        return result;
    }

    public String toJson(Object instance) throws IOException {
        return defaultObjectMapper.writeValueAsString(instance);
    }

    public <T> T fromJson(String json, Class<T> tClass) throws IOException {
        return defaultObjectMapper.readValue(json, tClass);
    }

    public ObjectMapper getDefaultObjectMapper() {
        return defaultObjectMapper;
    }
}
