package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorTest {
    /**
     * 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환 (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
     * 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다. 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
     * 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다
     */

    @DisplayName("빈 문자열이나 Null 값의 경우 0을 반환한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void input_null_and_empty_text(final String input) {
        final int result = Calculator.sum(input);
        assertThat(result).isZero();
    }

    @DisplayName("쉼표 구분자로 덧셈을 한다.")
    @ParameterizedTest
    @ValueSource(strings = "1,2,3")
    void input_comma_text(final String input) {
        final int result = Calculator.sum(input);
        assertThat(result).isEqualTo(6);
    }

    @DisplayName("콜론 구분자로 덧셈을 한다.")
    @ParameterizedTest
    @ValueSource(strings = "1:2:3")
    void input_colon_text(final String input) {
        final int result = Calculator.sum(input);
        assertThat(result).isEqualTo(6);
    }

    @DisplayName("쉼표 및 콜론 구분자로 덧셈을 한다.")
    @ParameterizedTest
    @ValueSource(strings = "1:2,3")
    void input_colon_and_comma_text(final String input) {
        final int result = Calculator.sum(input);
        assertThat(result).isEqualTo(6);
    }
}
