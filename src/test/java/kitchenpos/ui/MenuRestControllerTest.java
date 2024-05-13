package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

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
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("치킨류");

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("후라이드");
        product.setPrice(BigDecimal.valueOf(10000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(2);


        Menu request = new Menu();
        request.setName("후라이드+후라이드");
        request.setPrice(BigDecimal.valueOf(19000));
        request.setMenuGroupId(menuGroup.getId());
        request.setDisplayed(true);
        request.setMenuProducts(Arrays.asList(menuProduct));

        Menu response = new Menu();
        response.setName(request.getName());
        response.setId(UUID.randomUUID());

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
        UUID menuId = UUID.randomUUID();
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(15000));

        Menu response = new Menu();
        response.setId(menuId);
        response.setPrice(request.getPrice());

        given(menuService.changePrice(any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/menus/{menuId}/price", menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(response.getPrice()));


    }

    @Test
    void display() throws Exception {

        //given
        UUID menuId = UUID.randomUUID();
        Menu response = new Menu();
        response.setId(menuId);
        response.setDisplayed(false);


        given(menuService.display(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/menus/{menuId}/display", menuId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayed").value(false));
    }

    @Test
    void hide() throws Exception {
        UUID menuId = UUID.randomUUID();
        Menu response = new Menu();
        response.setId(menuId);
        response.setDisplayed(true);


        given(menuService.display(any()))
                .willReturn(response);

        //when then
        mockMvc.perform(put("/api/menus/{menuId}/display", menuId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayed").value(true));
    }

    @Test
    void findAll() throws Exception {

        //given
        Menu menu1 = new Menu();
        menu1.setId(UUID.randomUUID());
        menu1.setName("menu1");
        Menu menu2 = new Menu();
        menu2.setId(UUID.randomUUID());
        menu2.setName("menu2");

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