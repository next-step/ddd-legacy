package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.bo.MenuBo;
import kitchenpos.model.Menu;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MenuRestControllerTests {

    @InjectMocks
    private MenuRestController menuRestController;

    @Mock
    private MenuBo menuBo;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static Menu mockCreatedMenu;
    private static List<Menu> mockMenus = new ArrayList<>();

    @BeforeEach
    public void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(menuRestController).alwaysDo(print()).build();
    }

    @BeforeAll
    public static void setup() {
        mockCreatedMenu = new Menu();
        mockCreatedMenu.setId(1L);

        mockMenus.add(new Menu());
    }

    @DisplayName("알맞는 메뉴로 POST 요청 시 성공(201)")
    @Test
    public void postMenuSuccess() throws Exception {
        Menu mockRequestMenu = new Menu();
        mockRequestMenu.setMenuProducts(new ArrayList<>());
        given(menuBo.create(any(Menu.class))).willReturn(mockCreatedMenu);

        mockMvc.perform(post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mockRequestMenu)))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/menus/1"))
        ;
    }

    @DisplayName("GET 메뉴 콜렉션 요청 시 성공(200")
    @Test
    public void getMenusSuccess() throws Exception {
        given(menuBo.list()).willReturn(mockMenus);

        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[")))
                .andExpect(content().string(containsString("price")))
        ;
    }
}
