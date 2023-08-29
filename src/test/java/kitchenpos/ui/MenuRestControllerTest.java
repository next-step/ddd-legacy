package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.application.MenuService;
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
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuRestController.class)
class MenuRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("메뉴를 생성 API")
    void create() throws Exception {
        var request = TestFixture.createMenu("후라이드치킨", 16000L, true);
        var response = TestFixture.copy(request);
        given(menuService.create(any())).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        );

        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.price").value(response.getPrice()))
                .andReturn();
    }

    @Test
    @DisplayName("메뉴 목록 조회 API")
    void changePrice() throws Exception {
        var request = TestFixture.createMenu("후라이드치킨", 16000L);
        var response = TestFixture.copy(request);
        given(menuService.changePrice(any(), any())).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/api/menus/{menuId}/price", request.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.price").value(response.getPrice()))
                .andReturn();
    }

    @Test
    @DisplayName("메뉴 전시 API")
    void display() throws Exception {
        var menuId = UUID.randomUUID();
        var response = TestFixture.createMenu("후라이드치킨", true);
        given(menuService.display(any())).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/api/menus/{menuId}/display", menuId)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.price").value(response.getPrice()))
                .andReturn();
    }

    @Test
    @DisplayName("메뉴 숨김 API")
    void hide() throws Exception {
        var menuId = UUID.randomUUID();
        var response = TestFixture.createMenu("후라이드치킨", false);
        given(menuService.display(any())).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/api/menus/{menuId}/display", menuId)
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.price").value(response.getPrice()))
                .andReturn();
    }

    @Test
    @DisplayName("메뉴 목록 조회 API")
    void findAll() throws Exception {
        var response = List.of(
                TestFixture.createMenu("후라이드치킨1", 11000L, true),
                TestFixture.createMenu("후라이드치킨2", 12000L, false),
                TestFixture.createMenu("후라이드치킨3", 13000L, true)
        );
        given(menuService.findAll()).willReturn(response);

        var result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/menus")
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andReturn();
    }

}
