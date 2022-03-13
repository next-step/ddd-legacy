package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static kitchenpos.domain.MenuGroupFixture.CHICKEN_MENU_GROUP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private MenuGroupService menuGroupService;

    @Test
    @DisplayName("메뉴그룹 생성 테스트")
    void createMenuGroupTest() throws Exception {
        // given
        given(menuGroupService.create(any())).willReturn(CHICKEN_MENU_GROUP);

        // when
        mockMvc.perform(post("/api/menu-groups")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(CHICKEN_MENU_GROUP)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(CHICKEN_MENU_GROUP.getId().toString()))
               .andExpect(jsonPath("$.name").value(CHICKEN_MENU_GROUP.getName()))
               .andDo(print());
    }

    @Test
    @DisplayName("메뉴그룹 목록 조회 테스트")
    void getMenuGroupsTest() throws Exception {
        // given
        MenuGroup menuGroup1 = MenuGroupFixture.create("menuGroup1");
        MenuGroup menuGroup2 = MenuGroupFixture.create("menuGroup2");
        MenuGroup menuGroup3 = MenuGroupFixture.create("menuGroup3");

        given(menuGroupService.findAll()).willReturn(Arrays.asList(menuGroup1, menuGroup2, menuGroup3));

        // when
        mockMvc.perform(get("/api/menu-groups"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value(menuGroup1.getName()))
               .andExpect(jsonPath("$[1].name").value(menuGroup2.getName()))
               .andExpect(jsonPath("$[2].name").value(menuGroup3.getName()))
               .andDo(print());
    }
}
