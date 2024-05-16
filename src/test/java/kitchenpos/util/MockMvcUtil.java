package kitchenpos.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class MockMvcUtil {

    private MockMvcUtil() {
    }

    public static <T> T readValue(final ObjectMapper objectMapper, final MvcResult result, final Class<T> valueType) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), valueType);
    }

    public static <T> List<T> readListValue(final ObjectMapper objectMapper, final MvcResult result, final Class<T> valueType) throws Exception {
        final JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, valueType);

        return objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), javaType);
    }
}

