package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class CalculatorTest {


    @DisplayName("계산기는 문자열을 입력받는다")
    @ValueSource(strings = {"1,2,3", "1:2:3"})
    @ParameterizedTest
    void calculatorInputString(String input) {
        Assertions.assertThatCode(() -> Calculator.calculate(input))
                .doesNotThrowAnyException();
    }

    @DisplayName("계산기는 문자열과 구분자를 입력받는다")
    @Test
    void calculatorInputStringAndCustomDelimiter() {
        Assertions.assertThatCode(() -> Calculator.calculate("//&\n1&2&3"))
                .doesNotThrowAnyException();
    }

    @DisplayName("계산기는 입력 숫자 문자열의 합을 반환한다")
    @Test
    void calculatorReturnSum() {

        assertThat(Calculator.calculate("1,2,3")).isEqualTo(6);
    }

    @DisplayName("계산기는 숫자 이외의 문자열을 입력 받으면 예외 던진다")
    @Test
    void calculatorThrowRuntimeExceptionWhenNonNumberString() {

        assertThatThrownBy(() -> Calculator.calculate("A,B,C"))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("계산기는 음수를 입력 받으면 예외 던진다")
    @Test
    void calculatorThrowRuntimeExceptionWhenNegativeNumber() {

        assertThatThrownBy(() -> Calculator.calculate("1,2,-1"))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("0, 빈문자열, null이 입력되면 0을 반환한다")
    @ParameterizedTest
    @CsvSource(value = {"0", "null", "''"}, nullValues = "null")
    void shouldReturnZeroWhenInputIsNullOrEmpty(String inputString) {
        System.out.println("inputString = " + inputString);
        assertThat(Calculator.calculate(inputString)).isZero();
    }
}
