package calculator;

import calculator.domain.Digit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class DigitTest {

    @DisplayName(value = "숫자 1을 Digit 객체로 변환한다.")
    @Test
    void createDigit() {
        assertThat(Digit.from(1).getValue()).isSameAs(1);
    }

    @DisplayName(value = "Digit 객체에 음수가 들어갈 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void exception() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> Digit.from(-1));
    }

    @DisplayName(value = "두개의 숫자의 합을 반환한다.")
    @Test
    void addNumber() {
        Digit digit = Digit.from(1);
        Digit other = Digit.from(2);
        assertThat(digit.add(other).getValue()).isSameAs(3);
    }
}
