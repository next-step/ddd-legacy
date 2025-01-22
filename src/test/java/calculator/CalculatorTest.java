package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CalculatorTest {

    @ParameterizedTest
    @CsvSource({
            "'1,2,3', 6",
            "'10,20', 30",
            "'', 0",
            "'1', 1",
            "'''', 0"
    })
    @DisplayName("쉼표를 구분자로 구분된 문자열을 더할 수 있다.")
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
            "1:2:3, 6",
            "10:20, 30",
    })
    @DisplayName("콜론을 구분자로 구분된 문자열을 더할 수 있다.")
    void calculate_by_colon(String value, int expected) {
        // given
        Calculator calculator = new Calculator();

        // when
        int result = calculator.calculate(value);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "'//;\\n1;2;3', 6",
            "'//*\\n1*3', 4"
    }, delimiterString = ",")
    @DisplayName("커스텀 구분자로 구분된 가지는 문자열을 더할 수 있다.")
    void calculate_by_custom_delimiter(String value, int expected) {
        // given
        Calculator calculator = new Calculator();

        // when
        int result = calculator.calculate(value);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "숫자 외의 값",
            "1,a,3",
            "abc",
            "1,2,문자열,4"
    })
    @DisplayName("숫자 외의 값이 들어오면 예외를 던진다.")
    void validate_number(String value) {
        // given
        Calculator calculator = new Calculator();

        // when // then
        assertThatThrownBy(() -> calculator.calculate(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("숫자 외의 값을 넣을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-1:3",
            "1,-2,3",
            "-1,-2,-3"
    })
    @DisplayName("음수가 들어오면 예외를 던진다.")
    void validate_negative_number(String value) {
        // given
        Calculator calculator = new Calculator();

        // when // then
        assertThatThrownBy(() -> calculator.calculate(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음수를 넣을 수 없습니다.");
    }
}
