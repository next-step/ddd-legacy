package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static kitchenpos.fixture.MenuGroupFixtures.createMenuGroup;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuGroupService menuGroupService;

    private static final String BASE_URL = "/api/menu-groups";

    @DisplayName("메뉴 그룹을 생성한다")
    @Test
    void create() throws Exception {
        // given
        MenuGroup menuGroup = createMenuGroup();
        given(menuGroupService.create(any())).willReturn(menuGroup);

        // when
        ResultActions result = mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(menuGroup))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(menuGroup.getName()));
    }

    @DisplayName("모든 메뉴 그룹을 조회한다")
    @Test
    void findAll() throws Exception {
        // given
        MenuGroup menuGroup1 = createMenuGroup("메뉴 그룹1");
        MenuGroup menuGroup2 = createMenuGroup("메뉴 그룹1");
        given(menuGroupService.findAll()).willReturn(List.of(menuGroup1, menuGroup2));

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value(menuGroup1.getName()))
                .andExpect(jsonPath("$[1].name").value(menuGroup2.getName()));
    }
}
