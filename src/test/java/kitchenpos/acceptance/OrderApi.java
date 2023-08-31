package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderApi {
    private static final String API_ORDERS_URL = "/api/orders";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static MockHttpServletResponse 주문_생성_요청(MockMvc mockMvc, Map<String, Object> request) throws Exception {
        return mockMvc.perform(post(API_ORDERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();
    }

    public static void 주문_생성_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).contains(API_ORDERS_URL);
    }
}
