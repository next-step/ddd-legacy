package kitchenpos.ui;

import kitchenpos.BaseControllerTest;
import kitchenpos.commons.MenuGroupGenerator;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MenuGroupRestControllerTest extends BaseControllerTest {

    @Autowired
    private MenuGroupGenerator menuGroupGenerator;

    @Test
    @DisplayName("메뉴 그룹 등록 - 성공")
    void createMenuGroup() throws Exception {
        String name = "menu group 1";
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        ResultActions resultActions = mockMvc.perform(post("/api/menu-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menuGroup))
        ).andDo(print());

        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(name))
        ;
    }

    @Test
    @DisplayName("모든 제품 리스트 조회 - 성공")
    void findAllProduct() throws Exception {
        // given
        int size = 10;
        List<MenuGroup> menuGroups = IntStream.range(1, size).mapToObj(i -> menuGroupGenerator.generate()).collect(Collectors.toList());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/menu-groups")
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..['id']").exists())
                .andExpect(jsonPath("$..['name']").exists())
        ;
    }
}