package kitchenpos.acceptance;

import static kitchenpos.acceptance.MenuGroupApi.*;
import static kitchenpos.acceptance.MenuGroupData.*;

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
public class MenuGroupAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 메뉴그룹_생성__성공() throws Exception {
        Map<String, Object> request = 추천_메뉴.getValue();

        MockHttpServletResponse response = 메뉴그룹_생성_요청(mockMvc, request);

        메뉴그룹_생성_성공함(response);
    }

    @Test
    void 메뉴그룹_전체조회() throws Exception {
        Map<String, Object> request = 추천_메뉴.getValue();
        메뉴그룹_생성_요청(mockMvc, request);

        Map<String, Object> request2 = 금주의_할인_메뉴.getValue();
        메뉴그룹_생성_요청(mockMvc, request2);

        MockHttpServletResponse response = 메뉴그룹_전체조회_요청(mockMvc);

        메뉴그룹_전체조회_성공함(response, 2);
    }
}
