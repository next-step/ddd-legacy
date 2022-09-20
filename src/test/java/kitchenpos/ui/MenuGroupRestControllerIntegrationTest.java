package kitchenpos.ui;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MenuGroupRestControllerIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MenuGroupRepository menuGroupRepository;

  @AfterEach
  void tearDown() {
    menuGroupRepository.deleteAll();
  }

  @DisplayName("유효한 메뉴그룹 등록 요청에 응답을 201 Created와 함께 등록된 메뉴그룹을 반환한다")
  @Test
  void givenValidMenuGroup_whenCreateMenuGroup_thenStatus201WithMenuGroup() throws Exception {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setName("추천메뉴");

    mvc.perform(
            post("/api/menu-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menuGroup)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value(menuGroup.getName()));
  }

  @DisplayName("메뉴드룹 조회 요청에 응답으로 200 OK 응답과 함께 등록된 메뉴그룹을 반환한다")
  @Test
  void givenMenuGroups_whenFindAll_thenReturnMenuGroups() throws Exception {
    MenuGroup menuGroup1 = new MenuGroup();
    menuGroup1.setId(UUID.randomUUID());
    menuGroup1.setName("추천메뉴");

    MenuGroup menuGroup2 = new MenuGroup();
    menuGroup2.setId(UUID.randomUUID());
    menuGroup2.setName("점심특선");

    List.of(menuGroup1, menuGroup2)
        .forEach(menuGroupRepository::save);

    mvc.perform(get("/api/menu-groups").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value(menuGroup1.getName()))
        .andExpect(jsonPath("$[1].name").value(menuGroup2.getName()));
  }
}
