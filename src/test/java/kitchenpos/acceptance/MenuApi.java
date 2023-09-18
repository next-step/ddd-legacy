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

public class MenuApi {
    private static final String API_MENUS_URL = "/api/menus";
    private static final String API_MENUS_CHANGE_PRICE_URL = "/api/menus/{menuId}/price";
    private static final String API_MENUS_DISPLAY_URL = "/api/menus/{menuId}/display";
    private static final String API_MENUS_HIDE_URL = "/api/menus/{menuId}/hide";
    private static final String MENU_ID_EXTRACT_PATTERN_FROM_LOCATION = "/api/menus/([a-fA-F0-9-]+)";
    private static final Pattern MENU_ID_EXTRACT_PATTERN = Pattern.compile(MENU_ID_EXTRACT_PATTERN_FROM_LOCATION);
    private static final int CAPTURING_MENU_ID_INDEX = 1;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static MockHttpServletResponse 메뉴_생성_요청(MockMvc mockMvc, Map<String, Object> request) throws Exception {
        return mockMvc.perform(post(API_MENUS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();
    }

    public static void 메뉴_생성_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).contains(API_MENUS_URL);
    }

    public static MockHttpServletResponse 메뉴_가격_변경_요청(MockMvc mockMvc, String menuId, Map<String, Object> request) throws Exception {
        return mockMvc.perform(put(API_MENUS_CHANGE_PRICE_URL.replace("{menuId}", menuId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse();
    }

    public static void 메뉴_가격_변경_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    public static String extractMenuId(MockHttpServletResponse createdResponse) {
        Matcher matcher = MENU_ID_EXTRACT_PATTERN.matcher(createdResponse.getHeader("Location"));
        matcher.find();
        String menuId = matcher.group(CAPTURING_MENU_ID_INDEX);
        return menuId;
    }

    public static MockHttpServletResponse 메뉴_보이기_설정_요청(MockMvc mockMvc, String menuId) throws Exception {
        return mockMvc.perform(put(API_MENUS_DISPLAY_URL.replace("{menuId}", menuId)))
                .andReturn().getResponse();
    }

    public static void 메뉴_보이기_설정_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    public static MockHttpServletResponse 메뉴_숨기기_설정_요청(MockMvc mockMvc, String menuId) throws Exception {
        return mockMvc.perform(put(API_MENUS_HIDE_URL.replace("{menuId}", menuId)))
                .andReturn().getResponse();
    }

    public static void 메뉴_숨기기_설정_성공함(MockHttpServletResponse response) {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    public static MockHttpServletResponse 메뉴_전체조회_요청(MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(API_MENUS_URL))
                .andReturn().getResponse();
    }

    public static void 메뉴_전체조회_성공함(MockHttpServletResponse response, int expectedSize) throws Exception {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        List<Object> menus = objectMapper.readValue(response.getContentAsString(), List.class);
        assertThat(menus).hasSize(expectedSize);
    }

    public static String 메뉴_생성(MockMvc mockMvc, Map<String, Object> request) throws Exception {
        return extractMenuId(메뉴_생성_요청(mockMvc, request));
    }
}
