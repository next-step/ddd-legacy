package string_additional_calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StringAdditionalCalculator 클래스")
class StringAdditionalCalculatorTest {

    private final StringAdditionalCalculator stringAdditionalCalculator = new StringAdditionalCalculator();

    @DisplayName("쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환한다.")
    @Test
    void calculate() {
        // given
        final String expression = "1,2:3";

        // when
        int result = stringAdditionalCalculator.calculate(expression);

        // then
        assertThat(result).isEqualTo(6);
    }

    @DisplayName("기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void customSeparate() {
        // given
        final String expression = "//;\\n1;2;3";

        // when
        int result = stringAdditionalCalculator.calculate(expression);

        // then
        assertThat(result).isEqualTo(6);
    }

    @DisplayName("커스텀 구분자는 한 글자만 가능하다")
    @Test
    void invalidCustomSeparate() {
        // given
        final String expression = "//;e\\n1;e2;e3";

        // when then
        assertThatThrownBy(() -> stringAdditionalCalculator.calculate(expression))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("커스텀 구분자는 1글자여야 합니다. expression: //;e\\n1;e2;e3, separator: ;e");
    }


    @DisplayName("문자열 계산기에 숫자 이외의 값을 전달하는 경우 RuntimeException 예외를 throw 한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "1:2:한글사랑|문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. expression: 1:2:한글사랑, numbers: [1, 2, 한글사랑]",
            "1:2:@|문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. expression: 1:2:@, numbers: [1, 2, @]",
            "//;\\n1;2;eng|문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. expression: //;\\n1;2;eng, numbers: [1, 2, eng]"
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
            "-1,2:3|문자열 계산기에 상수는 음수가 될 수 없습니다. expression: -1,2:3, numbers: [-1, 2, 3]",
            "//;\\n-1;2;3|문자열 계산기에 상수는 음수가 될 수 없습니다. expression: //;\\n-1;2;3, numbers: [-1, 2, 3]"
    }, delimiter = '|')
    void invalidNumber(String expression, String exceptionMessage) {
        // when then
        assertThatThrownBy(() -> stringAdditionalCalculator.calculate(expression))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(exceptionMessage);
    }
}
