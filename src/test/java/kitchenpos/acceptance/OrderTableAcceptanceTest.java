package kitchenpos.acceptance;

import static kitchenpos.acceptance.OrderTableApi.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderTableAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 주문테이블_생성__성공() throws Exception {
        Map<String, Object> request = Map.of("name", "1번 테이블");

        MockHttpServletResponse response = 주문테이블_생성_요청(mockMvc, request);

        주문테이블_생성_성공함(response);
    }

    @Test
    void 주문테이블_착석__성공() throws Exception {
        String orderTableId = 주문테이블_생성(mockMvc, Map.of("name", "1번 테이블"));

        MockHttpServletResponse response = 주문테이블_착석_요청(mockMvc, orderTableId);

        주문테이블_착석_성공함(response);
    }

    @Test
    void 주문테이블_정리__성공() throws Exception {
        String orderTableId = 주문테이블_생성(mockMvc, Map.of("name", "1번 테이블"));

        MockHttpServletResponse response = 주문테이블_정리_요청(mockMvc, orderTableId);

        주문테이블_정리_성공함(response);
    }
}
