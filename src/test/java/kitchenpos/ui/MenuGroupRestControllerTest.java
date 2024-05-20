package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.testfixture.MenuGroupTestFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

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

    @MockBean
    private MenuGroupService menuGroupService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create() throws Exception {
        //given
        MenuGroup request = MenuGroupTestFixture.createMenuGroupRequest("menu-group");
        MenuGroup response = MenuGroupTestFixture.createMenuGroup("menu-group");
        given(menuGroupService.create(any()))
                .willReturn(response);

        // when then
        mockMvc.perform(post("/api/menu-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()));

    }

    @Test
    void findAll() throws Exception {
        //given
        MenuGroup menuGroup1 = MenuGroupTestFixture.createMenuGroup("mg1");
        MenuGroup menuGroup2 = MenuGroupTestFixture.createMenuGroup("mg2");

        given(menuGroupService.findAll())
                .willReturn(Arrays.asList(menuGroup1, menuGroup2));

        //when then
        mockMvc.perform(get("/api/menu-groups")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(menuGroup1.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(menuGroup1.getName()))
                .andExpect(jsonPath("$[1].id").value(menuGroup2.getId().toString()))
                .andExpect(jsonPath("$[1].name").value(menuGroup2.getName()));

    }
}