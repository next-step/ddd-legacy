package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    private UUID menuId;

    private UUID menuGroupId;

    private UUID productId;

    private MenuGroup menuGroup;

    private Product product;

    private MenuProduct menuProduct;

    private Menu menu;

    @BeforeEach
    void setUp() {
        menuId = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");
        menuGroupId = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");
        productId = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
        menuGroup = MenuGroupFixture.menuGroup(menuGroupId, "한마리메뉴");
        product = ProductFixture.product(productId, "후라이드", new BigDecimal(16000));
        menuProduct = MenuProductFixture.menuProduct(1L, 1, product);
        menu = MenuFixture.menu("후라이드치킨", new BigDecimal(16000), menuGroup, List.of(menuProduct), true);
    }

    @Test
    void 메뉴_생성_요청이_성공하면_메뉴정보를_반환한다() throws Exception {
        when(menuService.create(any(Menu.class))).thenReturn(MenuFixture.menu(menuId, "후라이드치킨", new BigDecimal(16000), menuGroup, List.of(menuProduct), true));

        mockMvc.perform(post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(menuId.toString()));
    }

    @Test
    void 메뉴_가격_변경_요청이_성공하면_가격이_변경된다() throws Exception {
        when(menuService.changePrice(eq(menuId), any(Menu.class))).thenReturn(MenuFixture.menu(menuId, "후라이드치킨", new BigDecimal(18000), menuGroup, List.of(menuProduct), true));

        mockMvc.perform(put("/api/menus/{menuId}/price", menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(menuId.toString()));

    }

    @Test
    void 메뉴_숨김에서_표시_변경_요청이_성공하면_상태가_변경된다() throws Exception {
        when(menuService.display(menuId)).thenReturn(MenuFixture.menu(menuId, "후라이드치킨", new BigDecimal(18000), menuGroup, List.of(menuProduct), true));

        mockMvc.perform(put("/api/menus/{menuId}/display", menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(MenuFixture.menu(menuId, "후라이드치킨", new BigDecimal(18000), menuGroup, List.of(menuProduct), true)))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(menuId.toString()))
                .andExpect(jsonPath("$.displayed").value(true));
    }

    @Test
    void 메뉴_표시에서_숨김_변경_요청이_성공하면_상태가_변경된다() throws Exception {
        when(menuService.hide(menuId)).thenReturn(MenuFixture.menu(menuId, "후라이드치킨", new BigDecimal(18000), menuGroup, List.of(menuProduct), false));

        mockMvc.perform(put("/api/menus/{menuId}/hide", menuId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(MenuFixture.menu(menuId, "후라이드치킨", new BigDecimal(18000), menuGroup, List.of(menuProduct), false)))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayed").value(false));

    }

    @Test
    void 메뉴_전체_조회_요청이_성공하면_메뉴_목록을_반환한다() throws Exception {
        Menu findMenu = MenuFixture.menu(menuId, "후라이드치킨", new BigDecimal(18000), menuGroup, List.of(menuProduct), true);
        when(menuService.findAll()).thenReturn(List.of(findMenu));

        mockMvc.perform(get("/api/menus"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
