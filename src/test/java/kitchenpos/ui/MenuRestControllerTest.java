package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.testfixture.MenuProductTestFixture;
import kitchenpos.testfixture.MenuTestFixture;
import kitchenpos.testfixture.ProductTestFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuRestController.class)
class MenuRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create() throws Exception {

        //given
        Product product = ProductTestFixture.createProduct("후라이드치킨", 10000L);
        MenuProduct menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
        Menu request = MenuTestFixture.createMenuRequest("후라이드+후라이드", 19000L, true, List.of(menuProduct));
        Menu response = MenuTestFixture.createMenu("후라이드+후라이드", 19000L, true, List.of(menuProduct));

        given(menuService.create(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value(response.getName()));

    }

    @Test
    void changePrice() throws Exception {

        //given
        Product product = ProductTestFixture.createProduct("감자튀김", 15000L);
        MenuProduct menuProduct = MenuProductTestFixture.createMenuProductRequest(1L, 1L, product);
        Menu menu = MenuTestFixture.createMenu("감자튀김", 15000L, true, List.of(menuProduct));
        Menu response = MenuTestFixture.createMenu("감자튀김", 14000L, true, List.of(menuProduct));

        given(menuService.changePrice(any(), any()))
                .willReturn(response);
        menu.setPrice(BigDecimal.valueOf(14000));

        //when then
        mockMvc.perform(put("/api/menus/{menuId}/price", menu.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(response.getPrice()));

    }

    @Test
    void display() throws Exception {

        //given
        Menu menu = MenuTestFixture.createMenu("감자튀김", 15000L, false, List.of(new MenuProduct()));
        Menu response = MenuTestFixture.createMenu("감자튀김", 15000L, true, List.of(new MenuProduct()));

        given(menuService.display(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/menus/{menuId}/display",  menu.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayed").value(true));

    }

    @Test
    void hide() throws Exception {

        //given
        Menu menu = MenuTestFixture.createMenu("감자튀김", 15000L, true, List.of(new MenuProduct()));
        Menu response = MenuTestFixture.createMenu("감자튀김", 15000L, false, List.of(new MenuProduct()));

        given(menuService.hide(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/menus/{menuId}/hide", menu.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayed").value(false));

    }

    @Test
    void findAll() throws Exception {

        //given
        Menu menu1 = MenuTestFixture.createMenu("후라이드치킨", 17000L, true, List.of(new MenuProduct()));
        Menu menu2 = MenuTestFixture.createMenu("감자튀김", 15000L, true, List.of(new MenuProduct()));

        given(menuService.findAll())
                .willReturn(Arrays.asList(menu1, menu2));

        //when then
        mockMvc.perform(get("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(menu1.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(menu1.getName()))
                .andExpect(jsonPath("$[1].id").value(menu2.getId().toString()))
                .andExpect(jsonPath("$[1].name").value(menu2.getName()));

    }
}