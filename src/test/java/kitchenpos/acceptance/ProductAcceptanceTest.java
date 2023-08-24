package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductAcceptanceTest {
    private static final String API_PRODUCTS_URL = "/api/products";
    private static final String API_PRODUCTS_CHANGE_PRICE_URL = "/api/products/{productId}/price";
    private static final String PRODUCT_ID_EXTRACT_PATTERN_FROM_LOCATION = "/api/products/([a-fA-F0-9-]+)";
    private static final Pattern PRODUCT_ID_EXTRACT_PATTERN = Pattern.compile(PRODUCT_ID_EXTRACT_PATTERN_FROM_LOCATION);
    private static final int CAPTURING_PRODUCT_ID_INDEX = 1;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 상품_생성__성공() throws Exception {
        Map<String, Object> request = Map.of(
                "name", "강정치킨",
                "price", 18000);

        MockHttpServletResponse response = 상품_생성_요청(request);

        상품_생성_성공함(response);
    }

    @Test
    void 상품_가격_변경__성공() throws Exception {
        Map<String, Object> createRequest = Map.of(
                "name", "강정치킨",
                "price", 18000);
        MockHttpServletResponse createdResponse = 상품_생성_요청(createRequest);

        String productId = extractProductId(createdResponse);
        Map<String, Object> request = Map.of("price", 17000);

        MockHttpServletResponse response = 상품_가격_변경_요청(productId, request);

        상품_가격_변경_성공함(response);
    }

    @Test
    void 상품_전체조회() throws Exception {
        Map<String, Object> request = Map.of(
                "name", "강정치킨",
                "price", 18000);
        상품_생성_요청(request);

        Map<String, Object> request2 = Map.of(
                "name", "양념치킨",
                "price", 17000);
        상품_생성_요청(request2);

        MockHttpServletResponse response = 상품_전체조회_요청();

        상품_전체조회_성공함(response, 2);
    }

    private MockHttpServletResponse 상품_생성_요청(Map<String, Object> request) throws Exception {
        return mockMvc.perform(post(API_PRODUCTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();
    }

    private void 상품_생성_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).contains(API_PRODUCTS_URL);
    }

    private MockHttpServletResponse 상품_가격_변경_요청(String productId, Map<String, Object> request) throws Exception {
        return mockMvc.perform(put(API_PRODUCTS_CHANGE_PRICE_URL.replace("{productId}", productId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();
    }

    private void 상품_가격_변경_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    private String extractProductId(MockHttpServletResponse createdResponse) {
        Matcher matcher = PRODUCT_ID_EXTRACT_PATTERN.matcher(createdResponse.getHeader("Location"));
        matcher.find();
        String productId = matcher.group(CAPTURING_PRODUCT_ID_INDEX);
        return productId;
    }

    private MockHttpServletResponse 상품_전체조회_요청() throws Exception {
        return mockMvc.perform(get(API_PRODUCTS_URL))
                .andReturn().getResponse();
    }

    private void 상품_전체조회_성공함(MockHttpServletResponse response, int expectedSize) throws Exception {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        List<Object> menuGroups = objectMapper.readValue(response.getContentAsString(), List.class);
        assertThat(menuGroups).hasSize(expectedSize);
    }
}
