package kitchenpos.ui;

import kitchenpos.DummyData;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest extends DummyData {

    @Autowired
    private MockMvc webMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private MenuGroupService menuGroupService;

    @BeforeEach
    void utf8Filter() {
        this.webMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @Test
    void create() throws Exception {
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
                .andExpect(jsonPath("$.id").value(menuGroup.getId().toString()))
                .andExpect(jsonPath("$.name").value(menuGroup.getName()))
                .andExpect(redirectedUrl("/api/menu-groups/" + menuGroup.getId()));
    }

    @Test
    void findAll() throws Exception {
        given(menuGroupService.findAll()).willReturn(menuGroups);

        webMvc.perform(get("/api/menu-groups"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}