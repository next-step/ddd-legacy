package stringcalculator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositiveIntegerTest {

    @Test
    void invalid_negative() {
        assertThatThrownBy(() -> new PositiveInteger(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음수로 생성할 수 없습니다.");
    }
}
