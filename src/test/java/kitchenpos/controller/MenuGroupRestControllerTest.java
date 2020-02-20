package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.bo.MenuGroupBo;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuGroupBo menuGroupBo;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("메뉴 그룹을 생성할 수 있어야 한다.")
    @Test
    void create() throws Exception {
        // given
        MenuGroup requestMenuGroup = createUnregisteredMenuGroupWithName("Test Menu Group");
        MenuGroup responseMenuGroup = createRegisteredMenuGroupWithId(requestMenuGroup, new Random().nextLong());

        given(menuGroupBo.create(any(MenuGroup.class)))
                .willReturn(responseMenuGroup);

        // when
        ResultActions result = mockMvc.perform(post("/api/menu-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestMenuGroup)));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/menu-groups/" + responseMenuGroup.getId()))
                .andExpect(content().json(objectMapper.writeValueAsString(responseMenuGroup)));
    }

    @DisplayName("메뉴 그룹의 목록을 볼 수 있어야 한다.")
    @Test
    void list() throws Exception {
        // given
        MenuGroup menuGroup1 = createRegisteredMenuGroupWithId(1L);
        MenuGroup menuGroup2 = createRegisteredMenuGroupWithId(2L);

        given(menuGroupBo.list())
                .willReturn(Arrays.asList(menuGroup1, menuGroup2));

        // when
        ResultActions result = mockMvc.perform(get("/api/menus-groups"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(menuGroup1.getName())))
                .andExpect(content().string(containsString(menuGroup2.getName())));
    }

    private MenuGroup createUnregisteredMenuGroupWithName(String name) {
        return new MenuGroup() {{
            setName(name);
        }};
    }

    private MenuGroup createRegisteredMenuGroupWithId(Long menuGroupId) {
        MenuGroup menuGroup = createUnregisteredMenuGroupWithName("Test Menu Group " + menuGroupId);
        menuGroup.setId(menuGroupId);

        return menuGroup;
    }

    private MenuGroup createRegisteredMenuGroupWithId(MenuGroup unregisteredMenuGroup, Long menuGroupId) {
        return new MenuGroup() {{
            setId(menuGroupId);
            setName(unregisteredMenuGroup.getName());
        }};
    }
}
