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

public class OrderTableApi {
    private static final String API_ORDER_TABLE_URL = "/api/order-tables";

    private static final String ORDER_TABLE_ID_EXTRACT_PATTERN_FROM_LOCATION = "/api/order-tables/([a-fA-F0-9-]+)";
    private static final Pattern ORDER_TABLE_ID_EXTRACT_PATTERN = Pattern.compile(ORDER_TABLE_ID_EXTRACT_PATTERN_FROM_LOCATION);
    private static final int CAPTURING_ORDER_TABLE_ID_INDEX = 1;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static MockHttpServletResponse 주문테이블_생성_요청(MockMvc mockMvc, Map<String, Object> request) throws Exception {
        return mockMvc.perform(post(API_ORDER_TABLE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();
    }

    public static void 주문테이블_생성_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).contains(API_ORDER_TABLE_URL);
    }

    public static MockHttpServletResponse 주문테이블_전체조회_요청(MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(API_ORDER_TABLE_URL))
                .andReturn().getResponse();
    }

    public static void 주문테이블_전체조회_성공함(MockHttpServletResponse response, int expectedSize) throws Exception {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        List<Object> orderTables = objectMapper.readValue(response.getContentAsString(), List.class);
        assertThat(orderTables).hasSize(expectedSize);
    }

    private static String extractOrderTableId(MockHttpServletResponse createdResponse) {
        Matcher matcher = ORDER_TABLE_ID_EXTRACT_PATTERN.matcher(createdResponse.getHeader("Location"));
        matcher.find();
        String orderTableId = matcher.group(CAPTURING_ORDER_TABLE_ID_INDEX);
        return orderTableId;
    }

    public static String 주문테이블_생성(MockMvc mockMvc, Map<String, Object> request) throws Exception {
        return extractOrderTableId(주문테이블_생성_요청(mockMvc, request));
    }

}
