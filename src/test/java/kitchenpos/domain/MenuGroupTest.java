package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MenuGroupTest {
    @DisplayName("메뉴그룹은 이름을 가지고 있다.")
    @Test
    void properties() {
        final var menuGroup = new MenuGroup();
        menuGroup.setName("초밥");

        assertThat(menuGroup.getName()).isEqualTo("초밥");
    }
}
