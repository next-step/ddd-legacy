package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.fixture.MenuFixtures.createMenu;
import static kitchenpos.fixture.MenuFixtures.createMenuProduct;
import static kitchenpos.fixture.MenuGroupFixtures.createMenuGroup;
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

    private MenuProduct menuProduct;
    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        menuProduct = createMenuProduct();
        menuGroup = createMenuGroup();
    }

    @DisplayName("메뉴를 생성한다")
    @Test
    void create() throws Exception {
        // given
        Menu menu = createMenu(new BigDecimal("1000"), "메뉴", List.of(menuProduct), menuGroup, true);

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

    @DisplayName("메뉴의 가격을 바꾼다")
    @Test
    void changePrice() throws Exception {
        // given
        Menu menu = createMenu(new BigDecimal("2000"), "메뉴", List.of(menuProduct), menuGroup, true);

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

    @DisplayName("메뉴를 화면에 표시한다")
    @Test
    void display() throws Exception {
        // given
        Menu menu = createMenu(new BigDecimal("2000"), "메뉴", List.of(menuProduct), menuGroup, true);

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

    @DisplayName("메뉴를 화면에서 숨긴다")
    @Test
    void hide() throws Exception {
        // given
        Menu menu = createMenu(new BigDecimal("2000"), "메뉴", List.of(menuProduct), false);

        given(menuService.hide(any())).willReturn(menu);

        // when
        ResultActions result = mockMvc.perform(put(BASE_URL + "/{menuId}/hide", menu.getId().toString())
                .content(objectMapper.writeValueAsString(menu))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.displayed").value(false));
    }

    @DisplayName("메뉴를 모두 조회한다")
    @Test
    void findAll() throws Exception {
        // given
        Menu menu1 = createMenu(new BigDecimal("2000"), "메뉴1", List.of(menuProduct), false);
        Menu menu2 = createMenu(new BigDecimal("3000"), "메뉴2", List.of(menuProduct), false);

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
}
