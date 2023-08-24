package kitchenpos.acceptance;

import static kitchenpos.acceptance.ProductApi.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductAcceptanceTest {


    @Autowired
    private MockMvc mockMvc;

    @Test
    void 상품_생성__성공() throws Exception {
        Map<String, Object> request = Map.of(
                "name", "강정치킨",
                "price", 18000);

        MockHttpServletResponse response = 상품_생성_요청(mockMvc, request);

        상품_생성_성공함(response);
    }

    @Test
    void 상품_가격_변경__성공() throws Exception {
        Map<String, Object> createRequest = Map.of(
                "name", "강정치킨",
                "price", 18000);
        MockHttpServletResponse createdResponse = 상품_생성_요청(mockMvc, createRequest);

        String productId = extractProductId(createdResponse);
        Map<String, Object> request = Map.of("price", 17000);

        MockHttpServletResponse response = 상품_가격_변경_요청(mockMvc, productId, request);

        상품_가격_변경_성공함(response);
    }

    @Test
    void 상품_전체조회() throws Exception {
        Map<String, Object> request = Map.of(
                "name", "강정치킨",
                "price", 18000);
        상품_생성_요청(mockMvc, request);

        Map<String, Object> request2 = Map.of(
                "name", "양념치킨",
                "price", 17000);
        상품_생성_요청(mockMvc, request2);

        MockHttpServletResponse response = 상품_전체조회_요청(mockMvc);

        상품_전체조회_성공함(response, 2);
    }
}
