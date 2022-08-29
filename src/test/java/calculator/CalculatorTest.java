package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CalculatorTest {
    /**
     * 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환 (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
     * 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다. 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
     * 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다
     */

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @NullAndEmptySource
    @ParameterizedTest(name = "빈 문자열이나 Null 값의 경우 0을 반환한다.")
    void null_and_empty(final String input) {
        final int result = calculator.sum(input);
        assertThat(result).isZero();
    }

    @ParameterizedTest(name = "쉼표 및 콜론 구분자로 덧셈을 한다.")
    @ValueSource(strings = {"1,2,3", "1:2:3", "1:2,3"})
    void colon_and_comma(final String input) {
        final int result = calculator.sum(input);
        assertThat(result).isEqualTo(6);
    }

    @ParameterizedTest(name = "음수 및 숫자 이외의 값을 넣으면 RuntimeException 발생한다.")
    @ValueSource(strings = {"-1", "a,b,c"})
    void no_number_and_negative(final String input) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> calculator.sum(input));
    }

    @ParameterizedTest(name = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ValueSource(strings = {"//;\n1;2;3"})
    void custom(final String input) {
        final int result = calculator.sum(input);
        assertThat(result).isEqualTo(6);
    }
}