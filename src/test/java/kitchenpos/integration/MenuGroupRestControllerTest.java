package kitchenpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.config.IntegrationTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroupWithId;
import static kitchenpos.util.MockMvcUtil.readListValue;
import static kitchenpos.util.MockMvcUtil.readValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@IntegrationTest
class MenuGroupRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Test
    void create() throws Exception {
        MenuGroup menuGroup = createMenuGroup("추천메뉴");
        final MvcResult result = mockMvc.perform(post("/api/menu-groups")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(menuGroup)))
                .andReturn();

        menuGroup = readValue(objectMapper, result, MenuGroup.class);

        assertThat(result.getResponse().getHeader("Location")).isEqualTo("/api/menu-groups/" + menuGroup.getId());
        assertThat(menuGroup.getName()).isEqualTo("추천메뉴");
    }

    @Test
    void findAll() throws Exception {
        final MenuGroup menuGroup1 = menuGroupRepository.save(createMenuGroupWithId("추천메뉴"));
        final MenuGroup menuGroup2 = menuGroupRepository.save(createMenuGroupWithId("세트메뉴"));

        final MvcResult result = mockMvc.perform(get("/api/menu-groups")
                        .content(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        final List<MenuGroup> menuGroups = readListValue(objectMapper, result, MenuGroup.class);
        final List<UUID> menuGroupIds = menuGroups.stream()
                .map(MenuGroup::getId)
                .toList();

        assertAll(
                () -> assertThat(menuGroups).hasSize(2),
                () -> assertThat(menuGroupIds).contains(menuGroup1.getId(), menuGroup2.getId())
        );
    }
}
