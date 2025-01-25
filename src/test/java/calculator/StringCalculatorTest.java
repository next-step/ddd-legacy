package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class StringCalculatorTest {
    private StringCalculator calculator;

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> calculator.add("-1"));
    }

    @DisplayName(value = "문자열 계산기에 숫자 이외의 값을 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void wrong() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> calculator.add("text"));
    }
}

