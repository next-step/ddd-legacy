package kitchenpos.ui;

import static kitchenpos.application.MenuGroupFixture.메뉴판;
import static kitchenpos.application.MenuGroupFixture.세트메뉴;
import static kitchenpos.application.MenuGroupFixture.추천메뉴;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuGroupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("메뉴 그룹 관리")
@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest {

    private static final String MENU_GROUPS_URI = "/api/menu-groups";

    @Autowired
    private MockMvc webMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴그룹 생성")
    @Test
    void create() throws Exception {
        //given
        String body = objectMapper.writeValueAsString(세트메뉴);

        given(menuGroupService.create(any())).willReturn(세트메뉴);

        //when
        ResultActions resultActions = 그룹_생성_요청(body);

        //then
        resultActions
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value(세트메뉴.getName()));
    }

    @DisplayName("메뉴그룹 조회")
    @Test
    void findAll() throws Exception {
        //given
        given(menuGroupService.findAll()).willReturn(메뉴판);

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
