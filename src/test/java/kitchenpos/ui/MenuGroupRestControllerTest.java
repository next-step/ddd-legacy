package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuGroupService menuGroupService;

    @Test
    void givenMenuGroup_whenCreateMenuGroup_thenReturnMenuGroup() throws Exception {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("menuGroup1");
        given(menuGroupService.create(any())).willReturn(menuGroup);

        mockMvc.perform(post("/api/menu-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menuGroup)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("menuGroup1")))
                .andExpect(jsonPath("$.id", is(menuGroup.getId().toString())))
                .andExpect(header().string("Location", "/api/menu-groups/" + menuGroup.getId().toString()));
    }

    @Test
    void givenMenuGroups_whenGetMenuGroups_thenReturnMenuGroups() throws Exception {
        List<MenuGroup> menuGroups = new ArrayList<>();
        MenuGroup menuGroup1 = new MenuGroup();
        menuGroup1.setName("menuGroup1");
        MenuGroup menuGroup2 = new MenuGroup();
        menuGroup2.setName("menuGroup2");
        menuGroups.add(menuGroup1);
        menuGroups.add(menuGroup2);

        given(menuGroupService.findAll()).willReturn(menuGroups);

        mockMvc.perform(get("/api/menu-groups")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(menuGroup1.getName())))
                .andExpect(jsonPath("$[1].name", is(menuGroup2.getName())));
    }
}
