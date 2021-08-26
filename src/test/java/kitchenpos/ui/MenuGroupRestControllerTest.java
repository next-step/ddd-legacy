package kitchenpos.ui;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class MenuGroupRestControllerTest extends IntegrationTest {

    MenuGroup 독특한메뉴;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        독특한메뉴 = new MenuGroup();
        독특한메뉴.setName("독특한메뉴");
    }

    @DisplayName("메뉴 그룹을 생성한다")
    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/menu-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(독특한메뉴)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @DisplayName("메뉴 그룹 생성이 실패한다")
    @Test
    void createFailedByEmptyName() throws Exception {
        독특한메뉴.setName("");
        mockMvc.perform(post("/api/menu-groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(독특한메뉴)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @DisplayName("메뉴 그룹 목록을 조회한다")
    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/api/menu-groups"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));
    }
}
