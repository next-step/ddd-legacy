package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class MenuGroupTest {
    @DisplayName("연관 메뉴 생성")
    @Test
    void test1() {
        final UUID id = UUID.randomUUID();
        final String name = "치킨";

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);

        assertThat(menuGroup.getId()).isEqualTo(id);
        assertThat(menuGroup.getName()).isEqualTo(name);
    }
}