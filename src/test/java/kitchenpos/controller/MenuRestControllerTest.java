package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.bo.MenuBo;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuRestController.class)
class MenuRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuBo menuBo;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("메뉴 생성할 수 있어야 한다.")
    @Test
    void create() throws Exception {
        // given
        Menu requestMenu = createUnregisteredMenuWithName("Test Menu");
        Menu responseMenu = createRegisteredMenuWithId(requestMenu, new Random().nextLong());

        given(menuBo.create(any(Menu.class)))
                .willReturn(responseMenu);

        // when
        ResultActions result = mockMvc.perform(post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestMenu)));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/menus/" + responseMenu.getId()))
                .andExpect(content().json(objectMapper.writeValueAsString(responseMenu)));
    }

    @DisplayName("메뉴 목록을 볼 수 있어야 한다.")
    @Test
    void list() throws Exception {
        // given
        Menu menu1 = createRegisteredMenuWithId(1L);
        Menu menu2 = createRegisteredMenuWithId(2L);
        Menu menu3 = createRegisteredMenuWithId(3L);

        given(menuBo.list())
                .willReturn(Arrays.asList(menu1, menu2, menu3));

        // when
        ResultActions result = mockMvc.perform(get("/api/menus"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(menu1.getName())))
                .andExpect(content().string(containsString(menu2.getName())))
                .andExpect(content().string(containsString(menu3.getName())));
    }

    private Menu createUnregisteredMenuWithName(String name) {
        return new Menu() {{
            setName(name);
            setPrice(BigDecimal.valueOf(1000));
            setMenuGroupId(1L);
            setMenuProducts(Arrays.asList(
                    new MenuProduct() {{
                        setSeq(1L);
                        setProductId(3L);
                        setQuantity(2);
                    }},
                    new MenuProduct() {{
                        setSeq(2L);
                        setProductId(1L);
                        setQuantity(1);
                    }})
            );
        }};
    }

    private Menu createRegisteredMenuWithId(Long menuId) {
        Menu menu = createUnregisteredMenuWithName("Test Menu " + menuId);
        menu.setId(menuId);

        return menu;
    }

    private Menu createRegisteredMenuWithId(Menu unregisteredMenu, Long menuId) {
        return new Menu() {{
            setId(menuId);
            setName(unregisteredMenu.getName());
            setPrice(unregisteredMenu.getPrice());
            setMenuGroupId(unregisteredMenu.getMenuGroupId());
            setMenuProducts(unregisteredMenu.getMenuProducts());
        }};
    }
}
