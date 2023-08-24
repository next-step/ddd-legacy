package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuService;
import kitchenpos.application.MenuServiceTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.application.MenuGroupServiceTest.createMenuGroup;
import static kitchenpos.application.MenuServiceTest.createMenuProduct;
import static kitchenpos.application.ProductServiceTest.createProduct;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuRestController.class)
class MenuRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuService menuService;

    private static final String BASE_URL = "/api/menus";

    private Product product;
    private MenuProduct menuProduct;
    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        product = createProduct("햄버거", new BigDecimal("1000"));
        menuProduct = createMenuProduct(product, 1L);
        menuGroup = createMenuGroup("메뉴 그룹");
    }

    @Test
    void 메뉴를_생성한다() throws Exception {
        // given
        Menu menu = createMenu(new BigDecimal("1000"), "메뉴", menuGroup, true, List.of(menuProduct));

        given(menuService.create(any())).willReturn(menu);

        // when
        ResultActions result = mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(menu))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(menu.getName()))
                .andExpect(jsonPath("$.price").value(menu.getPrice()))
                .andExpect(jsonPath("$.displayed").value(menu.isDisplayed()))
                .andExpect(jsonPath("$.menuGroup.name").value(menuGroup.getName()))
                .andExpect(jsonPath("$.menuProducts[0].product.name").value(menuProduct.getProduct().getName()))
                .andExpect(jsonPath("$.menuProducts[0].product.price").value(menuProduct.getProduct().getPrice()))
                .andExpect(jsonPath("$.menuProducts[0].quantity").value(menuProduct.getQuantity()));
    }

    @Test
    void 메뉴의_가격을_바꾼다() throws Exception {
        // given
        Menu menu = createMenu(new BigDecimal("2000"), "메뉴", menuGroup, true, List.of(menuProduct));

        given(menuService.changePrice(any(), any())).willReturn(menu);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{menuId}/price", menu.getId().toString())
                .content(objectMapper.writeValueAsString(menu))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(menu.getPrice()));
    }

    @Test
    void 메뉴를_화면에_표시한다() throws Exception {
        // given
        Menu menu = createMenu(new BigDecimal("2000"), "메뉴", menuGroup, true, List.of(menuProduct));

        given(menuService.display(any())).willReturn(menu);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{menuId}/display", menu.getId().toString())
                .content(objectMapper.writeValueAsString(menu))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.displayed").value(true));
    }

    @Test
    void 메뉴를_화면에서_숨긴다() throws Exception {
        // given
        Menu menu = createMenu(new BigDecimal("2000"), "메뉴", menuGroup, false, List.of(menuProduct));

        given(menuService.display(any())).willReturn(menu);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{menuId}/display", menu.getId().toString())
                .content(objectMapper.writeValueAsString(menu))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.displayed").value(false));
    }

    @Test
    void 메뉴를_모두_조회한다() throws Exception {
        // given
        Menu menu1 = createMenu(new BigDecimal("2000"), "메뉴1", menuGroup, false, List.of(menuProduct));
        Menu menu2 = createMenu(new BigDecimal("3000"), "메뉴2", menuGroup, false, List.of(menuProduct));

        given(menuService.findAll()).willReturn(List.of(menu1, menu2));

        // when
        ResultActions result = mockMvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value(menu1.getName()))
                .andExpect(jsonPath("$[1].name").value(menu2.getName()));
    }

    private Menu createMenu(
            BigDecimal price,
            String name,
            MenuGroup menuGroup,
            boolean displayed,
            List<MenuProduct> menuProducts
    ) {
        Menu menu = MenuServiceTest.createMenu(price, name, menuProducts);
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(displayed);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);
        return menu;
    }
}
