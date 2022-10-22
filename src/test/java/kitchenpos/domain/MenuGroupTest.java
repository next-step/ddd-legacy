package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("메뉴 그룹")
class MenuGroupTest {

    @DisplayName("메뉴명은 공백일 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void menuNameNotNullAndEmpty(String name) {
        assertThatThrownBy(() -> new Name(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 이나 공백일 수 없습니다.");
    }
}
