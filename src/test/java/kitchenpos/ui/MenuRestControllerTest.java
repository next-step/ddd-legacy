package kitchenpos.ui;

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static kitchenpos.fixture.MenuFixture.TEST_MENU;
import static kitchenpos.fixture.OrderTableFixture.TEST_ORDER_TABLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MenuRestController.class)
@DisplayName("/api/menus 메뉴 ui 레이어 테스트")
class MenuRestControllerTest extends BaseRestControllerTest{

    @MockBean
    MenuService menuService;

    public static final String BASE_URL = "/api/menus";

    @Test
    @DisplayName("[POST] 메뉴를 등록한다.")
    void createTest() throws Exception {
        //given
        Menu menu = TEST_MENU();
        given(menuService.create(any())).willReturn(menu);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("price").exists())
                .andExpect(jsonPath("menuGroup").exists())
                .andExpect(jsonPath("displayed").exists())
                .andExpect(jsonPath("menuProducts").exists())
                .andExpect(jsonPath("menuGroupId").exists())
        ;
    }

    @Test
    @DisplayName("[PUT] /{menuId}/price 메뉴의 가격을 변경한다.")
    void changePriceTest() throws Exception {
        //given
        Menu menu = TEST_MENU();
        given(menuService.changePrice(any(), any())).willReturn(menu);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL +"/" + menu.getId() + "/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("price").exists())
                .andExpect(jsonPath("menuGroup").exists())
                .andExpect(jsonPath("displayed").exists())
                .andExpect(jsonPath("menuProducts").exists())
                .andExpect(jsonPath("menuGroupId").exists())
        ;
    }


    @Test
    @DisplayName("[PUT] /{menuId}/display 메뉴를 활성화한다.")
    void displayTest() throws Exception {
        //given
        Menu menu = TEST_MENU();
        given(menuService.display(any())).willReturn(menu);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL +"/" + menu.getId() + "/display")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("price").exists())
                .andExpect(jsonPath("menuGroup").exists())
                .andExpect(jsonPath("displayed").exists())
                .andExpect(jsonPath("menuProducts").exists())
                .andExpect(jsonPath("menuGroupId").exists())
        ;
    }


    @Test
    @DisplayName("[PUT] /{menuId}/hide 메뉴를 비활성화한다.")
    void hideTest() throws Exception {
        //given
        Menu menu = TEST_MENU();
        given(menuService.hide(any())).willReturn(menu);

        //when
        ResultActions resultActions = mockMvc.perform(
                put(BASE_URL +"/" + menu.getId() + "/hide")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("price").exists())
                .andExpect(jsonPath("menuGroup").exists())
                .andExpect(jsonPath("displayed").exists())
                .andExpect(jsonPath("menuProducts").exists())
                .andExpect(jsonPath("menuGroupId").exists())
        ;
    }
}