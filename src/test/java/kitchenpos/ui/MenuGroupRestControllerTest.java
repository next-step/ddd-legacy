package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.fixture.MenuGroupFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuGroupService menuGroupService;

    @Test
    void 메뉴_그룹_생성_요청이_성공하면_메뉴_그룹정보를_반환한다() throws Exception {
        when(menuGroupService.create(any(MenuGroup.class))).thenReturn(MenuGroupFixture.menuGroup(UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded"), "한마리메뉴"));

        mockMvc.perform(post("/api/menu-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(MenuGroupFixture.menuGroup(UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded"), "한마리메뉴"))))
                .andDo(print()).andExpect(status().isCreated());
    }

    @Test
    void 메뉴_그룹_전체_조회_요청이_성공하면_그룹_목록을_반환한다() throws Exception {
        when(menuGroupService.findAll()).thenReturn(List.of(MenuGroupFixture.menuGroup(UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded"), "한마리메뉴")));

        mockMvc.perform(get("/api/menu-groups"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
