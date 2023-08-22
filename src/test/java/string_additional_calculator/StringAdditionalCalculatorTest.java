package string_additional_calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StringAdditionalCalculator 클래스")
class StringAdditionalCalculatorTest {

    private final StringAdditionalCalculator stringAdditionalCalculator = new StringAdditionalCalculator(new ExpressionSeparator());

    @DisplayName("식이 null이거나 문자열인 경우 0을 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void expressionIsNull(String expression) {
        // when
        PositiveNumber result = stringAdditionalCalculator.calculate(expression);

        // then
        assertThat(result).isEqualTo(PositiveNumber.ZERO);
    }

    @DisplayName("쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환한다.")
    @Test
    void calculate() {
        // given
        final String expression = "1,2:3";

        // when
        PositiveNumber result = stringAdditionalCalculator.calculate(expression);

        // then
        assertThat(result).isEqualTo(PositiveNumber.from("6"));
    }

    @DisplayName("기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void customSeparate() {
        // given
        final String expression = "//;\n1;2;3";

        // when
        PositiveNumber result = stringAdditionalCalculator.calculate(expression);

        // then
        assertThat(result).isEqualTo(PositiveNumber.from("6"));
    }

    @DisplayName("커스텀 구분자는 한 글자만 가능하다")
    @Test
    void invalidCustomSeparate() {
        // given
        final String expression = "//두개\n1;e2;e3";

        // when then
        assertThatThrownBy(() -> stringAdditionalCalculator.calculate(expression))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("커스텀 구분자는 1글자여야 합니다. separator: 두개");
    }


    @DisplayName("문자열 계산기에 숫자 이외의 값을 전달하는 경우 RuntimeException 예외를 throw 한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "1:2:한글사랑|문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. number: 한글사랑",
            "1:2:@|문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. number: @",
    }, delimiter = '|')
    void givenNonNumberValue(String expression, String exceptionMessage) {
        // when then
        assertThatThrownBy(() -> stringAdditionalCalculator.calculate(expression))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(exceptionMessage);
    }

    @DisplayName("문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "-1,2:3|문자열 계산기에 상수는 음수가 될 수 없습니다. number: -1",
            "1,-2:3|문자열 계산기에 상수는 음수가 될 수 없습니다. number: -2",
            "1,2:-3|문자열 계산기에 상수는 음수가 될 수 없습니다. number: -3"
    }, delimiter = '|')
    void invalidNumber(String expression, String exceptionMessage) {
        // when then
        assertThatThrownBy(() -> stringAdditionalCalculator.calculate(expression))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(exceptionMessage);
    }
}
