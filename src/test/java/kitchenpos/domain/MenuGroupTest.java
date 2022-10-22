package kitchenpos.domain;

import kitchenpos.menu.menugroup.domain.MenuGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("메뉴 그룹")
class MenuGroupTest {

    @DisplayName("메뉴 그룹 생성")
    @ValueSource(strings = {"치킨"})
    void constructor(String name) {
        assertThatNoException().isThrownBy(() -> new MenuGroup(UUID.randomUUID(), new Name(name, false)));
    }

    @DisplayName("메뉴명은 공백일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void menuNameNotNullAndEmpty(String name) {
        assertThatThrownBy(() -> new MenuGroup(UUID.randomUUID(), new Name(name, false)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 이나 공백일 수 없습니다.");
    }
}
