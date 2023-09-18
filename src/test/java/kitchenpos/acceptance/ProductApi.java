package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductApi {
    private static final String API_PRODUCTS_URL = "/api/products";
    private static final String API_PRODUCTS_CHANGE_PRICE_URL = "/api/products/{productId}/price";
    private static final String PRODUCT_ID_EXTRACT_PATTERN_FROM_LOCATION = "/api/products/([a-fA-F0-9-]+)";
    private static final Pattern PRODUCT_ID_EXTRACT_PATTERN = Pattern.compile(PRODUCT_ID_EXTRACT_PATTERN_FROM_LOCATION);
    private static final int CAPTURING_PRODUCT_ID_INDEX = 1;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static MockHttpServletResponse 상품_생성_요청(MockMvc mockMvc, Map<String, Object> request) throws Exception {
        return mockMvc.perform(post(API_PRODUCTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();
    }

    public static void 상품_생성_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).contains(API_PRODUCTS_URL);
    }

    public static MockHttpServletResponse 상품_가격_변경_요청(MockMvc mockMvc, String productId, Map<String, Object> request) throws Exception {
        return mockMvc.perform(put(API_PRODUCTS_CHANGE_PRICE_URL.replace("{productId}", productId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();
    }

    public static void 상품_가격_변경_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    private static String extractProductId(MockHttpServletResponse createdResponse) {
        Matcher matcher = PRODUCT_ID_EXTRACT_PATTERN.matcher(createdResponse.getHeader("Location"));
        matcher.find();
        String productId = matcher.group(CAPTURING_PRODUCT_ID_INDEX);
        return productId;
    }

    public static MockHttpServletResponse 상품_전체조회_요청(MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(API_PRODUCTS_URL))
                .andReturn().getResponse();
    }

    public static void 상품_전체조회_성공함(MockHttpServletResponse response, int expectedSize) throws Exception {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        List<Object> menuGroups = objectMapper.readValue(response.getContentAsString(), List.class);
        assertThat(menuGroups).hasSize(expectedSize);
    }

    public static String 상품_생성(MockMvc mockMvc, Map<String, Object> request) throws Exception {
        return extractProductId(상품_생성_요청(mockMvc, request));
    }

}
