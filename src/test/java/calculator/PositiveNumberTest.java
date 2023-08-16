package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositiveNumberTest {

    @DisplayName("음수로 생성하면 예외가 발생한다")
    @Test
    void negativeNumber() {
        assertThatThrownBy(() -> new PositiveNumber(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("숫자가 아닌 문자열을 전달하면 예외가 발생한다")
    @Test
    void invalidNumber() {
        assertThatThrownBy(() -> PositiveNumber.fromString("a"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("값을 더해 새로운 숫자를 만든다")
    @Test
    void plus() {
        PositiveNumber number1 = new PositiveNumber(1);
        PositiveNumber number2 = new PositiveNumber(2);

        PositiveNumber actual = number1.plus(number2);

        assertThat(actual).isEqualTo(new PositiveNumber(3));
    }
}
