package calculator;

import org.apache.logging.log4j.util.Strings;
import org.assertj.core.api.Assertions;
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
    @ValueSource(strings = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
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


    @DisplayName("//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다")
    @ValueSource(strings = {"//;\n1;2;3"})
    @ParameterizedTest
    void customSeparator(String inputText) {
        final var calculator = new Calculator();
        assertThat(calculator.add(inputText)).isEqualTo(6);
    }

    @DisplayName("커스텀 구분자 이후 빈 값 입력 시 0을 반환")
    @ValueSource(strings = {"//;\n"})
    @ParameterizedTest
    void customSeparator2(String inputText) {
        final var calculator = new Calculator();
        assertThat(calculator.add(inputText)).isEqualTo(0);
    }

    @DisplayName("커스텀 구분자와 기존 구분자를 혼합하여 사용할 수 있다")
    @ValueSource(strings = {"//;\n1;2:3,4"})
    @ParameterizedTest
    void mixSeparator(String inputText) {
        final var calculator = new Calculator();
        assertThat(calculator.add(inputText)).isEqualTo(10);
    }

    @DisplayName("문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        Assertions.assertThatRuntimeException().isThrownBy(() -> {
            final var calculator = new Calculator();
            calculator.add("-1");
        });
    }
}
