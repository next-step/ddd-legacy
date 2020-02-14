package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kitchenpos.bo.MenuBo;
import kitchenpos.model.Menu;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MenuRestControllerTests {

    @InjectMocks
    private MenuRestController menuRestController;

    @Mock
    private MenuBo menuBo;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Menu mockCreatedMenu;
    private List<Menu> mockMenus = new ArrayList<>();

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(menuRestController).alwaysDo(print()).build();

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
