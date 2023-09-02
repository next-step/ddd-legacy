package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.fixture.MenuFixtures.createMenu;
import static kitchenpos.fixture.MenuFixtures.createMenuProduct;
import static kitchenpos.fixture.MenuGroupFixtures.createMenuGroup;
import static kitchenpos.fixture.ProductFixtures.createProduct;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuRestController.class)
class MenuRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuService menuService;
    Product product1;
    MenuProduct menuProduct1;
    Product product2;
    MenuProduct menuProduct2;
    MenuGroup menuGroup1;
    MenuGroup menuGroup2;

    @BeforeEach
    void setUp() {
        product1 = createProduct("상품1", new BigDecimal(1000));
        menuProduct1 = createMenuProduct(product1, 1);
        menuGroup1 = createMenuGroup("메뉴그룹1");
        product2 = createProduct("상품2", new BigDecimal(2000));
        menuProduct2 = createMenuProduct(product2, 2);
        menuGroup2 = createMenuGroup("메뉴그룹2");
    }

    @Test
    void 메뉴를_생성한다() throws Exception {
        //given
        Menu menu = createMenu("메뉴", new BigDecimal("1000"), menuGroup1, false, List.of(menuProduct1, menuProduct2));

        given(menuService.create(any()))
                .willReturn(menu);

        //when
        ResultActions result = mvc.perform(post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)));

        //then
        result.andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(menu.getId().toString()))
                .andExpect(jsonPath("$.name").value(menu.getName()))
                .andExpect(jsonPath("$.price").value(menu.getPrice()))
                .andExpect(jsonPath("$.menuGroup.id").value(menuGroup1.getId().toString()))
                .andExpect(jsonPath("$.menuGroup.name").value(menuGroup1.getName()))
                .andExpect(jsonPath("$.displayed").value(menu.isDisplayed()))
                .andExpect(jsonPath("$.menuProducts", hasSize(menu.getMenuProducts().size())))
                .andExpect(jsonPath("$.menuProducts[0].product.id").value(menuProduct1.getProduct().getId().toString()))
                .andExpect(jsonPath("$.menuProducts[0].product.name").value(menuProduct1.getProduct().getName()))
                .andExpect(jsonPath("$.menuProducts[0].product.price").value(menuProduct1.getProduct().getPrice()))
                .andExpect(jsonPath("$.menuProducts[0].quantity").value(menuProduct1.getQuantity()))
                .andExpect(jsonPath("$.menuProducts[1].product.id").value(menuProduct2.getProduct().getId().toString()))
                .andExpect(jsonPath("$.menuProducts[1].product.name").value(menuProduct2.getProduct().getName()))
                .andExpect(jsonPath("$.menuProducts[1].product.price").value(menuProduct2.getProduct().getPrice()))
                .andExpect(jsonPath("$.menuProducts[1].quantity").value(menuProduct2.getQuantity()));
    }

    @Test
    void 메뉴_가격을_변경한다() throws Exception {
        //given
        Menu menu = createMenu("메뉴", new BigDecimal("1000"), menuGroup1, false, List.of(menuProduct1, menuProduct2));

        given(menuService.changePrice(any(), any()))
                .willReturn(menu);
        //when
        ResultActions result = mvc.perform(put("/api/menus/{menuId}/price", menu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(menu.getPrice()));
    }

    @Test
    void 메뉴를_노출시킨다() throws Exception {
        //given
        Menu menu = createMenu("메뉴", new BigDecimal("1000"), menuGroup1, true, List.of(menuProduct1, menuProduct2));

        given(menuService.display(any()))
                .willReturn(menu);

        //when
        ResultActions result = mvc.perform(put("/api/menus/{menuId}/display", menu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.displayed").value(true));
    }

    @Test
    void 메뉴_노출을_중지한다() throws Exception {
        //given
        Menu menu = createMenu("메뉴", new BigDecimal("1000"), menuGroup1, false, List.of(menuProduct1, menuProduct2));

        given(menuService.hide(any()))
                .willReturn(menu);

        //when
        ResultActions result = mvc.perform(put("/api/menus/{menuId}/hide", menu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.displayed").value(false));
    }

    @Test
    void 모든_메뉴를_조회한다() throws Exception {
        //given
        Menu menu1 = createMenu("메뉴1", new BigDecimal("1000"), menuGroup1, false, List.of(menuProduct1));
        Menu menu2 = createMenu("메뉴2", new BigDecimal("2000"), menuGroup2, false, List.of(menuProduct2));

        List<Menu> menus = List.of(menu1, menu2);

        given(menuService.findAll())
                .willReturn(menus);

        //when
        ResultActions result = mvc.perform(get("/api/menus")
                .contentType(MediaType.APPLICATION_JSON));


        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(menus.size())))
                .andExpect(jsonPath("$[0].name").value(menu1.getName()))
                .andExpect(jsonPath("$[1].name").value(menu2.getName()));
    }

}