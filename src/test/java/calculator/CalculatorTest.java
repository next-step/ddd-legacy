package calculator;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatorTest {

    @DisplayName("빈 문자열 입력 시 0을 반환")
    @Test
    void empty() {
        Calculator calculator = new Calculator();
        assertThat(calculator.add(Strings.EMPTY)).isZero();
    }

    @DisplayName("null 입력 시 0을 반환")
    @Test
    void inputNull() {
        Calculator calculator = new Calculator();
        assertThat(calculator.add(null)).isZero();
    }
}