package kitchenpos.ui;

import kitchenpos.FixtureData;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest extends FixtureData {

    @Autowired
    private MockMvc webMvc;

    @MockBean
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        fixtureMenuGroups();

        this.webMvc = ofUtf8MockMvc();
    }

    @DisplayName("메뉴그룹 생성하기")
    @Test
    void createMenuGroup() throws Exception {
        MenuGroup menuGroup = menuGroups.get(0);

        given(menuGroupService.create(any())).willReturn(menuGroup);

        ResultActions perform = webMvc.perform(
                post("/api/menu-groups")
                .content("{\"name\":\"추천메뉴\"}")
                .contentType("application/json")
        );

        perform
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(menuGroup.getName()));
    }

    @DisplayName("메뉴그룹 전체조회")
    @Test
    void findAll() throws Exception {
        given(menuGroupService.findAll()).willReturn(menuGroups);

        webMvc.perform(get("/api/menu-groups"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}