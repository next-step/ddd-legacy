package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

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

    @DisplayName(value = "실제 값이 주어진 값과 동등한지 확인합니다.")
    @Test
    void isEqualToTest() {
        assertThat(new PositiveNumber(1)).isEqualTo(new PositiveNumber(1));
    }

    @DisplayName(value = "plus 계산이 제대로 동작하는지 확인 한다.")
    @Test
    void plusTest() {
        assertThat(new PositiveNumber(1).plus(new PositiveNumber(2))).isEqualTo(new PositiveNumber(3));
    }

    @DisplayName(value = "음수를 전달하는 경우 IllegalArgumentException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PositiveNumber(-1));
    }
}