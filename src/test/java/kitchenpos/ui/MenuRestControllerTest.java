package kitchenpos.ui;

import kitchenpos.FixtureData;
import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuRestController.class)
class MenuRestControllerTest extends FixtureData {

    @Autowired
    private MockMvc webMvc;

    @MockBean
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        fixtureMenus();

        this.webMvc = ofUtf8MockMvc();
    }

    @DisplayName("메뉴 생성하기")
    @Test
    void createMenu() throws Exception {
        Menu menu = menus.get(0);

        given(menuService.create(any())).willReturn(menu);

        ResultActions perform = webMvc.perform(
                post("/api/menus")
                .content(objectMapper.writeValueAsString(menu))
                .contentType("application/json")
        );

        perform
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.price").isNotEmpty())
                .andExpect(jsonPath("$.menuGroup.id").isNotEmpty())
                .andExpect(jsonPath("$.menuGroup.name").isNotEmpty())
                .andExpect(jsonPath("$.displayed").isNotEmpty())
                .andExpect(jsonPath("$.menuProducts").isNotEmpty());
    }

    @DisplayName("메뉴 가격 변경")
    @Test
    void changeMenuPrice() throws Exception {
        Menu changeMenu = new Menu();
        changeMenu.setPrice(ofPrice(1000));

        Menu menu = menus.get(0);
        menu.setPrice(changeMenu.getPrice());

        given(menuService.changePrice(any(), any())).willReturn(menu);

        ResultActions perform = webMvc.perform(
                put("/api/menus/{menuId}/price", menu.getId())
                        .content(objectMapper.writeValueAsString(changeMenu))
                        .contentType("application/json")
        );

        perform
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.price").value(changeMenu.getPrice()))
                .andExpect(jsonPath("$.menuGroup.id").isNotEmpty())
                .andExpect(jsonPath("$.menuGroup.name").isNotEmpty())
                .andExpect(jsonPath("$.displayed").isNotEmpty())
                .andExpect(jsonPath("$.menuProducts").isNotEmpty());
    }

    @DisplayName("메뉴 공개")
    @Test
    void menuDisplay() throws Exception {
        Menu menu = menus.get(0);

        given(menuService.display(any())).willReturn(menu);

        ResultActions perform = webMvc.perform(
                put("/api/menus/{menuId}/display", menu.getId())
        );

        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.price").isNotEmpty())
                .andExpect(jsonPath("$.menuGroup.id").isNotEmpty())
                .andExpect(jsonPath("$.menuGroup.name").isNotEmpty())
                .andExpect(jsonPath("$.displayed").value(MENU_SHOW))
                .andExpect(jsonPath("$.menuProducts").isNotEmpty());
    }

    @DisplayName("메뉴 비공개")
    @Test
    void menuHide() throws Exception {
        Menu menu = menus.get(0);
        menu.setDisplayed(MENU_HIDE);

        given(menuService.hide(any())).willReturn(menu);

        ResultActions perform = webMvc.perform(
                put("/api/menus/{menuId}/hide", menu.getId())
        );

        perform
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.price").isNotEmpty())
                .andExpect(jsonPath("$.menuGroup.id").isNotEmpty())
                .andExpect(jsonPath("$.menuGroup.name").isNotEmpty())
                .andExpect(jsonPath("$.displayed").value(MENU_HIDE))
                .andExpect(jsonPath("$.menuProducts").isNotEmpty());
    }

    @DisplayName("메뉴 전체조회")
    @Test
    void findAll() throws Exception {
        given(menuService.findAll()).willReturn(menus);

        webMvc.perform(get("/api/menus"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}