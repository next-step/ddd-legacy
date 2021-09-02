package kitchenpos.ui;

import kitchenpos.BaseControllerTest;
import kitchenpos.application.MenuService;
import kitchenpos.commons.MenuGenerator;
import kitchenpos.commons.MenuGroupGenerator;
import kitchenpos.commons.MenuProductGenerator;
import kitchenpos.commons.ProductGenerator;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MenuRestControllerTest extends BaseControllerTest {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuGenerator menuGenerator;
    @Autowired
    private MenuGroupGenerator menuGroupGenerator;
    @Autowired
    private ProductGenerator productGenerator;
    @Autowired
    private MenuProductGenerator menuProductGenerator;

    private MenuGroup menuGroup;
    private Product product;
    private MenuProduct menuProduct;
    private List<MenuProduct> menuProducts;
    private Menu menu;

    @Test
    @DisplayName("메뉴 등록 - 성공")
    void createMenuGroup() throws Exception {
        // given
        menuGroup = menuGroupGenerator.generate();

        product = productGenerator.generate();

        menuProduct = menuProductGenerator.generateRequestByProduct(product);

        menuProducts = new ArrayList<>();
        menuProducts.add(menuProduct);

        menu = generateMenuRequest();
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu))
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(menu.getName()))
                .andExpect(jsonPath("price").value(menu.getPrice()))
                .andExpect(jsonPath("menuGroup.id").exists())
                .andExpect(jsonPath("menuGroup.name").value(menuGroup.getName()))
                .andExpect(jsonPath("menuProducts[0].seq").exists())
                .andExpect(jsonPath("menuProducts[0].product.id").exists())
                .andExpect(jsonPath("menuProducts[0].product.name").value(product.getName()))
                .andExpect(jsonPath("menuProducts[0].product.price").value(product.getPrice().intValue()))
                .andExpect(jsonPath("menuProducts[0].quantity").value(menuProduct.getQuantity()))
        ;
    }

    private Menu generateMenuRequest() {
        Menu menu = new Menu();
        menu.setName("menu");
        menu.setPrice(BigDecimal.valueOf(2000));
        menu.setDisplayed(true);
        return menu;
    }

    @Test
    @DisplayName("메뉴 가격 변경 - 성공")
    void changePrice() throws Exception {
        // given
        menu = generateMenu();
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(1000));

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/menus/{menuId}/price", this.menu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(this.menu.getName()))
                .andExpect(jsonPath("price").value(request.getPrice()))
                .andExpect(jsonPath("displayed").value(true))
                .andExpect(jsonPath("menuGroup.id").exists())
                .andExpect(jsonPath("menuGroup.name").value(this.menuGroup.getName()))
                .andExpect(jsonPath("menuProducts[0].seq").exists())
                .andExpect(jsonPath("menuProducts[0].product.id").exists())
                .andExpect(jsonPath("menuProducts[0].product.name").value(product.getName()))
                .andExpect(jsonPath("menuProducts[0].product.price").value(product.getPrice().intValue()))
                .andExpect(jsonPath("menuProducts[0].quantity").value(menuProduct.getQuantity()))
        ;
    }

    private Menu generateMenu() {
        menuGroup = menuGroupGenerator.generate();

        product = productGenerator.generate();

        menuProduct = menuProductGenerator.generateRequestByProduct(product);

        menuProducts = new ArrayList<>();
        menuProducts.add(menuProduct);

        return menuGenerator.generateByMenuGroupAndMenuProducts(menuGroup, menuProducts);
    }

    @Test
    @DisplayName("메뉴 판매가능 - 성공")
    void displayMenu() throws Exception {
        // given
        menu = generateMenu();

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/menus/{menuId}/display", menu.getId())
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(this.menu.getName()))
                .andExpect(jsonPath("price").value(this.menu.getPrice().intValue()))
                .andExpect(jsonPath("displayed").value(true))
                .andExpect(jsonPath("menuGroup.id").exists())
                .andExpect(jsonPath("menuGroup.name").value(this.menuGroup.getName()))
                .andExpect(jsonPath("menuProducts[0].seq").exists())
                .andExpect(jsonPath("menuProducts[0].product.id").exists())
                .andExpect(jsonPath("menuProducts[0].product.name").value(product.getName()))
                .andExpect(jsonPath("menuProducts[0].product.price").value(product.getPrice().intValue()))
                .andExpect(jsonPath("menuProducts[0].quantity").value(menuProduct.getQuantity()))
        ;
    }

    @Test
    @DisplayName("메뉴 판매불가능 (숨기기) - 성공")
    void hideMenu() throws Exception {
        // given
        menu = generateMenu();
        menu = menuService.display(menu.getId());

        // when
        ResultActions resultActions = mockMvc.perform(put("/api/menus/{menuId}/hide", menu.getId())
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(this.menu.getName()))
                .andExpect(jsonPath("price").value(this.menu.getPrice().intValue()))
                .andExpect(jsonPath("displayed").value(false))
                .andExpect(jsonPath("menuGroup.id").exists())
                .andExpect(jsonPath("menuGroup.name").value(this.menuGroup.getName()))
                .andExpect(jsonPath("menuProducts[0].seq").exists())
                .andExpect(jsonPath("menuProducts[0].product.id").exists())
                .andExpect(jsonPath("menuProducts[0].product.name").value(product.getName()))
                .andExpect(jsonPath("menuProducts[0].product.price").value(product.getPrice().intValue()))
                .andExpect(jsonPath("menuProducts[0].quantity").value(menuProduct.getQuantity()))
        ;
    }

    @Test
    @DisplayName("모든 메뉴 리스트 조회 - 성공")
    void findAllMenu() throws Exception {
        // given
        int size = 10;
        List<Menu> menus = IntStream.range(1, size).mapToObj(i -> generateMenu()).collect(Collectors.toList());

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/menus")
        ).andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..['id']").exists())
                .andExpect(jsonPath("$..['name']").exists())
                .andExpect(jsonPath("$..['price']").exists())
                .andExpect(jsonPath("$..['displayed']").exists())
                .andExpect(jsonPath("$..['menuGroup'].id").exists())
                .andExpect(jsonPath("$..['menuGroup'].name").exists())
                .andExpect(jsonPath("$..['menuProducts'][0].seq").exists())
                .andExpect(jsonPath("$..['menuProducts'][0].product.id").exists())
                .andExpect(jsonPath("$..['menuProducts'][0].product.name").exists())
                .andExpect(jsonPath("$..['menuProducts'][0].product.price").exists())
                .andExpect(jsonPath("$..['menuProducts'][0].quantity").exists())
        ;
    }
}