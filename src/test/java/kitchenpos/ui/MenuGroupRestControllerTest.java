package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static kitchenpos.fixture.MenuGroupFixtures.createMenuGroup;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuGroupService menuGroupService;

    @Test
    void 메뉴그룹을_생성한다() throws Exception {
        //given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹1");

        given(menuGroupService.create(any()))
                .willReturn(menuGroup);

        //when
        ResultActions result = mvc.perform(post("/api/menu-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menuGroup)));

        //then
        result.andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(menuGroup.getId().toString()))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.name").value(menuGroup.getName()));
    }

    @Test
    void 모든_메뉴그룹을_조회한다() throws Exception {
        //given
        MenuGroup menuGroup1 = createMenuGroup("메뉴그룹1");
        MenuGroup menuGroup2 = createMenuGroup("메뉴그룹2");

        List<MenuGroup> menuGroup = List.of(menuGroup1, menuGroup2);

        given(menuGroupService.findAll())
                .willReturn(menuGroup);

        //when
        ResultActions result = mvc.perform(get("/api/menu-groups")
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value(menuGroup1.getName()))
                .andExpect(jsonPath("$[1].name").value(menuGroup2.getName()));
    }


}