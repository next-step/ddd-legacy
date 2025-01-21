package calculator;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatorTest {
    @DisplayName("빈 문자열 입력 시 0을 반환")
    @Test
    void empty() {
        final var calculator = new Calculator();
        assertThat(calculator.add(Strings.EMPTY)).isZero();
    }

    @DisplayName("null 입력 시 0을 반환")
    @Test
    void inputNull() {
        final var calculator = new Calculator();
        assertThat(calculator.add(null)).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환")
    @ValueSource(strings = {"1", "2", "3", "4", "5", "6", "7", "8", "9"})
    @ParameterizedTest
    void oneNumber(String text) {
        final var calculator = new Calculator();
        assertThat(calculator.add(text)).isEqualTo(Integer.parseInt(text));
    }

    @DisplayName("숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환")
    @CsvSource(value = {"1,2^3", "8,9^17"}, delimiter = '^')
    @ParameterizedTest
    void twoNumbers1(String inputText, int result) {
        final var calculator = new Calculator();
        assertThat(calculator.add(inputText)).isEqualTo(result);
    }

    @DisplayName("숫자 두개를 쉼표(:) 구분자로 입력할 경우 두 숫자의 합을 반환")
    @CsvSource(value = {"1,2^3", "8,9^17"}, delimiter = '^')
    @ParameterizedTest
    void twoNumbers2(String inputText, int result) {
        final var calculator = new Calculator();
        assertThat(calculator.add(inputText)).isEqualTo(result);
    }
}