package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PositiveNumberTest {

    @DisplayName(value = "음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negativeNumberTest() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new PositiveNumber("-1"));
    }

    @DisplayName(value = "숫자가 아닌 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void nonNumberTest() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new PositiveNumber("abc"));
    }
}