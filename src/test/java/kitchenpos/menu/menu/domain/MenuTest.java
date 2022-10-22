package kitchenpos.menu.menu.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("메뉴")
class MenuTest {

    @DisplayName("메뉴 그룹에 속해 있다.")
    @Test
    void requireMenuGroup() {
        assertThatThrownBy(() -> new Menu(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 그룹이 없습니다.");
    }
}
