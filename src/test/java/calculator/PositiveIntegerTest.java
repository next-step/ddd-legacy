package calculator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositiveIntegerTest {

    @Test
    @DisplayName("음수가 들어오면 예외를 던진다")
    public void negative() {
        assertThatThrownBy(() -> new PositiveInteger(-1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("숫자가 아닌 값이 들어오면 예외를 던진다")
    public void not_number() {
        assertThatThrownBy(() -> new PositiveInteger("a"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
