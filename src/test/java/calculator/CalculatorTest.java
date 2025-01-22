package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculatorTest {

    @ParameterizedTest
    @CsvSource({
            "'1,2,3', 6",
            "'10,20', 30",
            "'', 0",
            "'1', 1",
            "'''', 0"
    })
    @DisplayName("쉼표를 구분자로 가지는 문자열을 더할 수 있다.")
    void calculate_by_comma(String value, int expected) {
        // given
        Calculator calculator = new Calculator();

        // when
        int result = calculator.calculate(value);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "'1:2:3', 6",
            "'10:20', 30",
    })
    @DisplayName("콜론을 구분자로 가지는 문자열을 더할 수 있다.")
    void calculate_by_colon(String value, int expected) {
        // given
        Calculator calculator = new Calculator();

        // when
        int result = calculator.calculate(value);

        // then
        assertThat(result).isEqualTo(expected);
    }

}
