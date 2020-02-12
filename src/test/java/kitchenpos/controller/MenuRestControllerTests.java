package kitchenpos.controller;

import kitchenpos.bo.MenuBo;
import kitchenpos.model.Menu;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuRestController.class)
class MenuRestControllerTests {

    @MockBean
    private MenuBo menuBo;

    @Autowired
    private MockMvc mockMvc;

    private static Menu mockCreatedMenu;

    @BeforeAll
    public static void setup() {
        mockCreatedMenu = new Menu();
        mockCreatedMenu.setId(1L);
    }

    @DisplayName("알맞는 메뉴로 POST 요청 시 성공(201)")
    @Test
    public void postMenuSuccess() throws Exception {
        given(menuBo.create(any(Menu.class))).willReturn(mockCreatedMenu);

        mockMvc.perform(post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\": \"후라이드+후라이드\",\n" +
                        "  \"price\": 19000,\n" +
                        "  \"menuGroupId\": 1,\n" +
                        "  \"menuProducts\": [\n" +
                        "    {\n" +
                        "      \"productId\": 1,\n" +
                        "      \"quantity\": 2\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/menus/1"))
        ;
    }
}
