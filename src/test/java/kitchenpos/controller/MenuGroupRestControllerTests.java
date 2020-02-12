package kitchenpos.controller;

import kitchenpos.bo.MenuGroupBo;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTests {

    @MockBean
    private MenuGroupBo menuGroupBo;

    @Autowired
    private MockMvc mockMvc;

    private MenuGroup mockMenuGroup;

    @BeforeEach
    public void setup() {
        mockMenuGroup = new MenuGroup();
    }

    @DisplayName("정상적인 MenuGroup으로 POST 요청 실행 시 성공")
    @ParameterizedTest
    @ValueSource(strings = {"testMenu", "testMenu2"})
    public void createMenuGroup(String menuGroupName) throws Exception {
        mockMenuGroup.setId(1L);
        mockMenuGroup.setName(menuGroupName);
        given(menuGroupBo.create(any(MenuGroup.class))).willReturn(mockMenuGroup);

        mockMvc.perform(post("/api/menu-groups").contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\": \"" + menuGroupName + "\"\n" +
                        "}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/menu-groups/" + mockMenuGroup.getId()))
                .andExpect(jsonPath("$.name", is(menuGroupName)))
        ;
    }

    @DisplayName("MenuGroup 전체 GET 요청 시 정보 확인 가능")
    @ParameterizedTest
    @ValueSource(strings = {"testMenu, testMenu2"})
    public void getMenuGroupCollection(String menuGroupName) throws Exception {
        mockMenuGroup.setName(menuGroupName);
        given(menuGroupBo.list()).willReturn(Collections.singletonList(mockMenuGroup));

        mockMvc.perform(get("/api/menus-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name", is(menuGroupName)))
        ;
    }
}
