package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MenuGroupApi {
    private static final String API_MENU_GROUPS_URL = "/api/menu-groups";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static MockHttpServletResponse 메뉴그룹_생성_요청(MockMvc mockMvc, Map<String, Object> request) throws Exception {
        return mockMvc.perform(post(API_MENU_GROUPS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();
    }

    public static void 메뉴그룹_생성_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).contains(API_MENU_GROUPS_URL);
    }

    public static MockHttpServletResponse 메뉴그룹_전체조회_요청(MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(API_MENU_GROUPS_URL))
                .andReturn().getResponse();
    }

    public static void 메뉴그룹_전체조회_성공함(MockHttpServletResponse response, int expectedSize) throws Exception {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        List<Object> menuGroups = objectMapper.readValue(response.getContentAsString(), List.class);
        assertThat(menuGroups).hasSize(expectedSize);
    }

}
