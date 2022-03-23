package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import kitchenpos.application.MenuService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static kitchenpos.domain.MenuFixture.CHICKEN_MENU;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuRestController.class)
class MenuRestControllerTest {

    public static final String BASE_URL = "/api/menus/";
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private MenuService menuService;

    @Test
    @DisplayName("메뉴 생성 테스트")
    void createMenuTest() throws Exception {
        // given
        given(menuService.create(any())).willReturn(CHICKEN_MENU);

        // when
        mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(CHICKEN_MENU)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(CHICKEN_MENU.getId().toString()))
               .andExpect(jsonPath("$.name").value(CHICKEN_MENU.getName()))
               .andDo(print());
    }

    @Test
    @DisplayName("가격 변경 테스트")
    void changePriceTest() throws Exception {
        // given
        given(menuService.changePrice(any(), any())).willReturn(CHICKEN_MENU);

        // when
        mockMvc.perform(put(BASE_URL + CHICKEN_MENU.getId().toString() + "/price")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(CHICKEN_MENU)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(CHICKEN_MENU.getId().toString()))
               .andExpect(jsonPath("$.name").value(CHICKEN_MENU.getName()))
               .andDo(print());
    }

    @Test
    @DisplayName("메뉴 전시 테스트")
    void displayTest() throws Exception {
        // given
        given(menuService.display(any())).willReturn(CHICKEN_MENU);

        // when
        mockMvc.perform(put(BASE_URL + CHICKEN_MENU.getId().toString() + "/display")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(CHICKEN_MENU)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(CHICKEN_MENU.getId().toString()))
               .andExpect(jsonPath("$.name").value(CHICKEN_MENU.getName()))
               .andDo(print());
    }

    @Test
    @DisplayName("메뉴 비전시 테스트")
    void hideTest() throws Exception {
        // given
        given(menuService.hide(any())).willReturn(CHICKEN_MENU);

        // when
        mockMvc.perform(put(BASE_URL + CHICKEN_MENU.getId().toString() + "/hide")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(CHICKEN_MENU)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(CHICKEN_MENU.getId().toString()))
               .andExpect(jsonPath("$.name").value(CHICKEN_MENU.getName()))
               .andDo(print());
    }

    @Test
    @DisplayName("메뉴 조회 테스트")
    void getTest() throws Exception {
        // given
        given(menuService.findAll()).willReturn(Collections.singletonList(CHICKEN_MENU));

        // when
        mockMvc.perform(get(BASE_URL))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(CHICKEN_MENU.getId().toString()))
               .andExpect(jsonPath("$[0].name").value(CHICKEN_MENU.getName()))
               .andDo(print());
    }
}
