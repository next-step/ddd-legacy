package kitchenpos.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kitchenpos.application.MenuGroupService;
import kitchenpos.config.TestConfig;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MenuGroupRestController.class)
@Import(TestConfig.class)
class MenuGroupRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MenuGroupService menuGroupService;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("메뉴 그룹 생성")
    @Nested
    class CreateMenuGroup {


        @DisplayName("메뉴 그룹 생성 실패하면 400 Bad Request를 응답한다.")
        @Test
        void if_failed_then_responds_400_bad_request() throws Exception {
            // given
            var name = "";
            var body = new HashMap<String, Object>() {{
                put("name", name);
            }};
            var content = objectMapper.writeValueAsString(body);
            given(menuGroupService.create(any())).willThrow(new IllegalArgumentException());

            // when
            mockMvc.perform(
                            post("/api/menu-groups")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isBadRequest());
        }


        @DisplayName("메뉴 그룹 생성 성공하면 201 Created를 응답한다.")
        @Test
        void if_succeeds_then_responds_201_created() throws Exception {
            // given
            var menuGroup = new MenuGroup();
            var name = "치킨";
            var body = new HashMap<String, Object>() {{
                put("name", name);
            }};
            var content = objectMapper.writeValueAsString(body);
            given(menuGroupService.create(any())).willReturn(menuGroup);

            // when
            mockMvc.perform(
                            post("/api/menu-groups")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(content))
                    // then
                    .andExpect(status().isCreated());
        }
    }

    @DisplayName("메뉴 그룹 목록 조회")
    @Nested
    class ListMenuGroups {

        @DisplayName("메뉴 그룹 목록 조회 성공하면 200 OK를 응답한다.")
        @Test
        void if_succeeds_then_responds_200_ok() throws Exception {
            // given
            var menuGroup1 = new MenuGroup();
            menuGroup1.setId(UUID.randomUUID());
            menuGroup1.setName("치킨");

            var menuGroup2 = new MenuGroup();
            menuGroup2.setId(UUID.randomUUID());
            menuGroup2.setName("사이드 메뉴");

            given(menuGroupService.findAll())
                    .willReturn(List.of(menuGroup1, menuGroup2));

            // when
            var result = mockMvc.perform(get("/api/menu-groups"))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            var responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            List<Map<String, String>> menuGroups = objectMapper.readValue(
                    responseContent,
                    new TypeReference<>() {
                    }
            );
            Assertions.assertAll(
                    () -> assertThat(menuGroups).hasSize(2),
                    () -> assertThat(menuGroups).extracting("name")
                            .containsExactly("치킨", "사이드 메뉴")
            );
        }
    }
}
