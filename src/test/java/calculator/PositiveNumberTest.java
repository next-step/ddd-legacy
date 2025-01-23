package calculator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositiveNumberTest {

    @Test
    @DisplayName("숫자 값이 음수라면 예외를 던진다.")
    void negative_exception() {
        // when // then
        assertThatThrownBy(() -> new PositiveNumber(new Number(-1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음수를 넣을 수 없습니다.");
    }
}
