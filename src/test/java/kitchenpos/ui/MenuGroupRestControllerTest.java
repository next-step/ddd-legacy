package kitchenpos.ui;

import static kitchenpos.application.MenuGroupFixture.세트메뉴;
import static kitchenpos.application.MenuGroupFixture.추천메뉴;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("메뉴 그룹 관리")
@WebMvcTest(MenuGroupRestController.class)
@Import(MenuGroupTestConfig.class)
class MenuGroupRestControllerTest {

    private static final String MENU_GROUPS_URI = "/api/menu-groups";

    @Autowired
    private MockMvc webMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("메뉴 그룹을 생성할 수 있다.")
    @Test
    void create() throws Exception {
        //given
        String body = objectMapper.writeValueAsString(세트메뉴);

        //when
        ResultActions resultActions = 그룹_생성_요청(body);

        //then
        resultActions
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value(세트메뉴.getName()));
    }

    @DisplayName("등록된 모든 메뉴 그룹을 조회할 수 있다.")
    @Test
    void findAll() throws Exception {
        //when
        ResultActions resultActions = 그룹_조회_요청();

        //then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value(세트메뉴.getName()))
            .andExpect(jsonPath("$[1].name").value(추천메뉴.getName()));
    }

    private ResultActions 그룹_생성_요청(String body) throws Exception {
        return webMvc.perform(post(MENU_GROUPS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print());
    }

    private ResultActions 그룹_조회_요청() throws Exception {
        return webMvc.perform(get(MENU_GROUPS_URI)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print());
    }
}
