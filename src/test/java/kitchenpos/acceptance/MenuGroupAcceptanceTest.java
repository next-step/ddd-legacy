package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;
import java.util.Map;

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
public class MenuGroupAcceptanceTest {
    private static final String API_MENU_GROUPS_URL = "/api/menu-groups";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 메뉴그룹_생성__성공() throws Exception {
        Map<String, Object> request = Map.of("name", "추천 메뉴");

        MockHttpServletResponse response = 메뉴그룹_생성_요청(request);

        메뉴그룹_생성_성공함(response);
    }

    @Test
    void 메뉴그룹_전체조회() throws Exception {
        Map<String, Object> request = Map.of("name", "추천 메뉴");
        메뉴그룹_생성_요청(request);

        Map<String, Object> request2 = Map.of("name", "금주의 할인 메뉴");
        메뉴그룹_생성_요청(request2);

        MockHttpServletResponse response = 메뉴그룹_전체조회_요청();

        메뉴그룹_전체조회_성공함(response, 2);
    }

    private MockHttpServletResponse 메뉴그룹_생성_요청(Map<String, Object> request) throws Exception {
        return mockMvc.perform(post(API_MENU_GROUPS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();
    }

    private void 메뉴그룹_생성_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).contains(API_MENU_GROUPS_URL);
    }

    private MockHttpServletResponse 메뉴그룹_전체조회_요청() throws Exception {
        return mockMvc.perform(get(API_MENU_GROUPS_URL))
                .andReturn().getResponse();
    }

    private void 메뉴그룹_전체조회_성공함(MockHttpServletResponse response, int expectedSize) throws Exception {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        List<Object> menuGroups = objectMapper.readValue(response.getContentAsString(), List.class);
        assertThat(menuGroups).hasSize(expectedSize);
    }
}
