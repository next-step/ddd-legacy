package kitchenpos.application.fake.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class JacksonFixtureBuilder<T> implements FixtureBuilder<T> {

    @Override
    public T build() {
        Type clazz = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) clazz).getActualTypeArguments()[0];
        ObjectMapper mapper = new ObjectMapper();
        JavaType _type = mapper.getTypeFactory().constructType(type);
        try {
            String content = mapper.writeValueAsString(this);
            return mapper.readValue(content, _type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }


}
