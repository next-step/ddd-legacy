package kitchenpos.ui;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static kitchenpos.fixture.MenuGroupFixture.TEST_MENU_GROUP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuGroupRestController.class)
@DisplayName("/api/menu-groups 메뉴 그룹 ui 레이어 테스트")
class MenuGroupRestControllerTest extends BaseRestControllerTest{

    @MockBean
    private MenuGroupService menuGroupService;

    private static final String BASE_URL = "/api/menu-groups";

    @Test
    @DisplayName("[POST] 메뉴 그룹을 등록한다.")
    void createTest() throws Exception {
        //given
        MenuGroup menuGroup = TEST_MENU_GROUP();
        given(menuGroupService.create(any())).willReturn(menuGroup);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuGroup))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
        ;
    }
}
