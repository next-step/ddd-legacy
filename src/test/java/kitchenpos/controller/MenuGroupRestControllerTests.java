package kitchenpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.bo.MenuGroupBo;
import kitchenpos.model.MenuGroup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MenuGroupRestControllerTests {

    @InjectMocks
    private MenuGroupRestController menuGroupRestController;

    @Mock
    private MenuGroupBo menuGroupBo;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private MenuGroup mockMenuGroup;

    @BeforeAll
    public void setupMockMvc() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(menuGroupRestController)
                .alwaysDo(print())
                .build();

        mockMenuGroup = new MenuGroup();
    }

    @DisplayName("정상적인 MenuGroup으로 POST 요청 실행 시 성공")
    @ParameterizedTest
    @ValueSource(strings = {"testMenu", "testMenu2"})
    public void createMenuGroup(String menuGroupName) throws Exception {
        mockMenuGroup.setId(1L);
        mockMenuGroup.setName(menuGroupName);
        given(menuGroupBo.create(any(MenuGroup.class))).willReturn(mockMenuGroup);

        mockMvc.perform(post("/api/menu-groups").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mockMenuGroup)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/menu-groups/" + mockMenuGroup.getId()))
                .andExpect(jsonPath("$.name", is(menuGroupName)))
        ;
    }

    @DisplayName("MenuGroup 전체 GET 요청 시 정보 확인 가능")
    @ParameterizedTest
    @ValueSource(strings = {"testMenu, testMenu2"})
    public void getMenuGroupCollection(String menuGroupName) throws Exception {
        mockMenuGroup.setName(menuGroupName);
        given(menuGroupBo.list()).willReturn(Collections.singletonList(mockMenuGroup));

        mockMvc.perform(get("/api/menus-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name", is(menuGroupName)))
        ;
    }
}
