package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberTest {

    @DisplayName("음수로 생성하면 예외가 발생한다")
    @Test
    void negativeNumber() {
        assertThatThrownBy(() -> Number.from("-1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("숫자가 아닌 문자열을 전달하면 예외가 발생한다")
    @Test
    void invalidNumber() {
        assertThatThrownBy(() -> Number.from("a"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("값을 더해 새로운 숫자를 만든다")
    @Test
    void plus() {
        Number number1 = Number.from("1");
        Number number2 = Number.from("2");

        Number actual = number1.plus(number2);

        assertThat(actual).isEqualTo(Number.from("3"));
    }
}
