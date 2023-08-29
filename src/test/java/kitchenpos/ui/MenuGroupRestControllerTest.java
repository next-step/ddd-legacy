package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.application.MenuGroupService;
import kitchenpos.testfixture.TestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MenuGroupRestController.class)
class MenuGroupRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuGroupService menuGroupService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("메뉴 그룹 생성 API")
    void create() throws Exception {
        var request = TestFixture.createMenuGroup("테스트");
        var response = TestFixture.createMenuGroup(request.getId(), request.getName());
        given(menuGroupService.create(any())).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/menu-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andReturn();
    }

    @Test
    @DisplayName("메뉴 그룹 목록 조회 API")
    void findAll() throws Exception {
        var response = List.of(
                TestFixture.createMenuGroup("테스트1"),
                TestFixture.createMenuGroup("테스트2"),
                TestFixture.createMenuGroup("테스트3")
        );
        given(menuGroupService.findAll()).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/menu-groups")
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andReturn();
    }

}
