package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderApi {
    private static final String API_ORDERS_URL = "/api/orders";
    private static final String API_ORDERS_ACCEPT_URL = "/api/orders/{orderId}/accept";
    private static final String API_ORDERS_SERVE_URL = "/api/orders/{orderId}/serve";
    private static final String API_ORDERS_START_DELIVERY_URL = "/api/orders/{orderId}/start-delivery";
    private static final String API_ORDERS_COMPLETE_URL = "/api/orders/{orderId}/complete";
    private static final String ORDER_ID_EXTRACT_PATTERN_FROM_LOCATION = "/api/orders/([a-fA-F0-9-]+)";
    private static final Pattern ORDER_ID_EXTRACT_PATTERN = Pattern.compile(ORDER_ID_EXTRACT_PATTERN_FROM_LOCATION);
    private static final int CAPTURING_ORDER_ID_INDEX = 1;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static MockHttpServletResponse 주문_생성_요청(MockMvc mockMvc, Map<String, Object> request) throws Exception {
        return mockMvc.perform(post(API_ORDERS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();
    }

    public static void 주문_생성_성공함(MockHttpServletResponse response) throws Exception {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).contains(API_ORDERS_URL);
        Map<String, Object> responseBody = extractResponseBody(response);
        assertThat(responseBody.get("status")).isEqualTo("WAITING");
    }

    public static String extractOrderId(MockHttpServletResponse createdResponse) {
        Matcher matcher = ORDER_ID_EXTRACT_PATTERN.matcher(createdResponse.getHeader("Location"));
        matcher.find();
        String orderId = matcher.group(CAPTURING_ORDER_ID_INDEX);
        return orderId;
    }

    public static MockHttpServletResponse 주문_수락_요청(MockMvc mockMvc, String orderId) throws Exception {
        return mockMvc.perform(put(API_ORDERS_ACCEPT_URL.replace("{orderId}", orderId)))
                .andReturn().getResponse();
    }

    public static void 주문_수락됨(MockHttpServletResponse response) throws Exception {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Map<String, Object> responseBody = extractResponseBody(response);
        assertThat(responseBody.get("status")).isEqualTo("ACCEPTED");
    }

    public static MockHttpServletResponse 주문_서빙_요청(MockMvc mockMvc, String orderId) throws Exception {
        return mockMvc.perform(put(API_ORDERS_SERVE_URL.replace("{orderId}", orderId)))
                .andReturn().getResponse();
    }

    public static void 주문_서빙됨(MockHttpServletResponse response) throws Exception {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Map<String, Object> responseBody = extractResponseBody(response);
        assertThat(responseBody.get("status")).isEqualTo("SERVED");
    }

    public static MockHttpServletResponse 주문_배달_시작_요청(MockMvc mockMvc, String orderId) throws Exception {
        return mockMvc.perform(put(API_ORDERS_START_DELIVERY_URL.replace("{orderId}", orderId)))
                .andReturn().getResponse();
    }

    public static void 주문_배달_시작됨(MockHttpServletResponse response) throws Exception {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Map<String, Object> responseBody = extractResponseBody(response);
        assertThat(responseBody.get("status")).isEqualTo("DELIVERING");
    }

    public static MockHttpServletResponse 주문_완료_요청(MockMvc mockMvc, String orderId) throws Exception {
        return mockMvc.perform(put(API_ORDERS_COMPLETE_URL.replace("{orderId}", orderId)))
                .andReturn().getResponse();
    }

    public static void 주문_완료됨(MockHttpServletResponse response) throws Exception {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Map<String, Object> responseBody = extractResponseBody(response);
        assertThat(responseBody.get("status")).isEqualTo("COMPLETED");
    }

    private static Map<String, Object> extractResponseBody(MockHttpServletResponse response) throws Exception {
        return objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
    }
}
