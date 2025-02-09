package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MenuRestController.class)
class MenuRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MenuService menuService;

    @Test
    void create() throws Exception {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("menu1");
        menu.setPrice(BigDecimal.valueOf(10000));
        given(menuService.create(any())).willReturn(menu);

        mockMvc.perform(post("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(menu.getName())))
                .andExpect(jsonPath("$.id", is(menu.getId().toString())))
                .andExpect(jsonPath("$.price", is(menu.getPrice().intValue())))
                .andExpect(header().string("Location", "/api/menus/" + menu.getId().toString()));
    }

    @Test
    void changePrice() throws Exception {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(BigDecimal.valueOf(10000));

        given(menuService.changePrice(any(), any())).willReturn(menu);
        menu.setPrice(BigDecimal.valueOf(20000));

        mockMvc.perform(put("/api/menus/" + menu.getId() + "/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(menu.getId().toString())))
                .andExpect(jsonPath("$.price", is(20000)));
    }

    @Test
    void display() throws Exception {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(false);

        given(menuService.display(any())).willReturn(menu);
        menu.setDisplayed(true);

        mockMvc.perform(put("/api/menus/" + menu.getId() + "/display")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(menu.getId().toString())))
                .andExpect(jsonPath("$.displayed", is(true)));
    }

    @Test
    void hide() throws Exception {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);

        given(menuService.hide(any())).willReturn(menu);
        menu.setDisplayed(false);

        mockMvc.perform(put("/api/menus/" + menu.getId() + "/hide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(menu.getId().toString())))
                .andExpect(jsonPath("$.displayed", is(false)));
    }

    @Test
    void findAll() throws Exception {
        List<Menu> menus = new ArrayList<>();
        Menu menu1 = new Menu();
        menu1.setName("menu1");
        Menu menu2 = new Menu();
        menu2.setName("menu2");
        menus.add(menu1);
        menus.add(menu2);

        given(menuService.findAll()).willReturn(menus);

        mockMvc.perform(get("/api/menus")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(menu1.getName())))
                .andExpect(jsonPath("$[1].name", is(menu2.getName())));
    }
}
