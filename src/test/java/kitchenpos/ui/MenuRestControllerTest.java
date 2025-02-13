package kitchenpos.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import kitchenpos.application.MenuService;
import kitchenpos.config.TestConfig;
import kitchenpos.domain.Menu;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MenuRestController.class)
@Import(TestConfig.class)
class MenuRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MenuService menuService;

    @DisplayName("메뉴 가격 변경")
    @Nested
    class ChangePrice {

        @DisplayName("메뉴 가격 변경 성공하면 200 OK를 응답한다.")
        @Test
        void change_price_if_succeeds_then_responds_200_ok() throws Exception {
            // given
            var menuId = UUID.randomUUID();
            var body = new HashMap<String, Object>() {{
                put("price", 0);
            }};
            var content = objectMapper.writeValueAsString(body);

            var menu = new Menu();
            menu.setId(menuId);

            given(menuService.changePrice(any(), any()))
                    .willReturn(menu);

            // when
            MvcResult mvcResult = mockMvc.perform(put("/api/menus/" + menuId + "/price")
                                                          .contentType("application/json")
                                                          .content(content))
                    // then
                    .andExpect(status().isOk())
                    .andReturn();

            var response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            var responseMenu = objectMapper.readValue(response, Menu.class);
            assertThat(responseMenu.getId()).isEqualTo(menuId);
        }

        @DisplayName("메뉴 가격 변경 실패하면 400 Bad Request를 응답한다.")
        @Test
        void change_price_if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var menuId = UUID.randomUUID();
            var body = new HashMap<String, Object>() {{
                put("price", 0);
            }};
            var content = objectMapper.writeValueAsString(body);

            given(menuService.changePrice(any(), any()))
                    .willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(put("/api/menus/" + menuId + "/price")
                                    .contentType("application/json")
                                    .content(content))
                    // then
                    .andExpect(status().isBadRequest());
        }
    }


    @DisplayName("메뉴 생성")
    @Nested
    class CreatMenu {

        @DisplayName("메뉴 생성 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var body = new HashMap<String, Object>() {{
                put("name", "");
                put("price", 0);
                put("menuGroupId", UUID.randomUUID());
                put("menuProducts", List.of(new HashMap<String, Object>() {{
                    put("productId", UUID.randomUUID());
                    put("quantity", 0);
                }}));
            }};
            var content = objectMapper.writeValueAsString(body);

            given(menuService.create(any()))
                    .willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(post("/api/menus")
                                    .contentType("application/json")
                                    .content(content))
                    // then
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("메뉴 생성 성공하면 201 Created를 응답한다.")
        @Test
        void if_succeeds_then_responds_201_created() throws Exception {
            // given
            var menu = new Menu();
            var body = new HashMap<String, Object>() {{
                put("name", "치킨");
                put("price", 16000);
                put("menuGroupId", UUID.randomUUID());
                put("menuProducts", List.of(new HashMap<String, Object>() {{
                    put("productId", UUID.randomUUID());
                    put("quantity", 1);
                }}));
            }};
            var content = objectMapper.writeValueAsString(body);

            given(menuService.create(any()))
                    .willReturn(menu);

            // when
            var result = mockMvc.perform(post("/api/menus")
                                                          .contentType("application/json")
                                                          .content(content))
                    // then
                    .andExpect(status().isCreated())
                    .andReturn();
            var response = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            var responseMenu = objectMapper.readValue(response, Menu.class);
            assertThat(responseMenu.getId()).isEqualTo(menu.getId());

            var uri = result.getResponse().getHeader("Location");
            assertThat(uri).isEqualTo("/api/menus/" + menu.getId());
        }
    }

    @DisplayName("메뉴 활성화")
    @Nested
    class DisplayMenu {

        @DisplayName("메뉴 활성화 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeeds_then_responds_200_ok() throws Exception {
            // given
            var menuId = UUID.randomUUID();
            var menu = new Menu();
            menu.setId(menuId);

            given(menuService.display(any()))
                    .willReturn(menu);

            // when
            MvcResult mvcResult = mockMvc.perform(put("/api/menus/" + menuId + "/display"))
                    // then
                    .andExpect(status().isOk())
                    .andReturn();

            var response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            var responseMenu = objectMapper.readValue(response, Menu.class);
            assertThat(responseMenu.getId()).isEqualTo(menuId);
        }

        @DisplayName("메뉴 활성화 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var menuId = UUID.randomUUID();

            given(menuService.display(any()))
                    .willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(put("/api/menus/" + menuId + "/display"))
                    // then
                    .andExpect(status().isBadRequest());
        }
    }

    @DisplayName("메뉴 비활성화")
    @Nested
    class HideMenu {

        @DisplayName("메뉴 비활성화 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeeds_then_responds_200_ok() throws Exception {
            // given
            var menuId = UUID.randomUUID();
            var menu = new Menu();
            menu.setId(menuId);

            given(menuService.hide(any()))
                    .willReturn(menu);

            // when
            MvcResult mvcResult = mockMvc.perform(put("/api/menus/" + menuId + "/hide"))
                    // then
                    .andExpect(status().isOk())
                    .andReturn();

            var response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            var responseMenu = objectMapper.readValue(response, Menu.class);
            assertThat(responseMenu.getId()).isEqualTo(menuId);
        }

        @DisplayName("메뉴 비활성화 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var menuId = UUID.randomUUID();

            given(menuService.hide(any()))
                    .willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(put("/api/menus/" + menuId + "/hide"))
                    // then
                    .andExpect(status().isBadRequest());
        }
    }

    @DisplayName("메뉴 목록 조회")
    @Nested
    class FindAllMenus {

        @DisplayName("메뉴 목록 조회 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeeds_then_responds_200_ok() throws Exception {
            // given
            var menu = new Menu();
            menu.setId(UUID.randomUUID());

            given(menuService.findAll())
                    .willReturn(List.of(menu));

            // when
            MvcResult mvcResult = mockMvc.perform(get("/api/menus"))
                    // then
                    .andExpect(status().isOk())
                    .andReturn();

            var response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            var responseMenu = objectMapper.readValue(response, List.class);
            assertThat(responseMenu).hasSize(1);
        }
    }
}
