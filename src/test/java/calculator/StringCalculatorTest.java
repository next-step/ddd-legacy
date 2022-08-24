package calculator;

import static org.assertj.core.api.Assertions.*;

import calculator.delimiter.ColonDelimiter;
import calculator.delimiter.CommaDelimiter;
import calculator.delimiter.CustomDelimiter;
import calculator.delimiter.Delimiters;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환 (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
 * 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다.
 *      예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
 * 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
 */
class StringCalculatorTest {

    private Delimiters delimiters;

    @BeforeEach
    void setup() {
        delimiters = new Delimiters(Arrays.asList(
            new ColonDelimiter(),
            new CommaDelimiter(),
            new CustomDelimiter()
        ));
    }
    @DisplayName("빈 문자열 또는 null을 입력한 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void empty_and_null_value_is_zero(final String expression) {
        assertThat(StringCalculator.calculate(expression, delimiters) == 0);
    }

    @DisplayName("숫자 하나를 문자열로 입력한 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "2", "34"})
    void not_contain_delimiter(final String expression) {
        assertThat(StringCalculator.calculate(expression, delimiters) == Integer.parseInt(expression));
    }

    @DisplayName("쉼표, 콜론을 구분자로 문자열을 전달하는 경우 숫자를 분리하고 합을 반환")
    @ParameterizedTest
    @MethodSource("provideNormalExpressions")
    void summary_by_normal_expressions(final String expression, int summary) {
        assertThat(StringCalculator.calculate(expression, delimiters) == summary);
    }

    private static Stream<Arguments> provideNormalExpressions() {
        return Stream.of(
            Arguments.of("1,2", 3),
            Arguments.of("1:2:3", 6),
            Arguments.of("1:2,3", 6)
        );
    }

    @DisplayName("'\\'와 '\n'의 사이에 위치하는 문자는 custom 구분자로 사용이 가능하며, 합을 반환한다.")
    @ParameterizedTest
    @MethodSource("provideCustomExpressions")
    void custom_delimiter(final String expression, int summary) {
        assertThat(StringCalculator.calculate(expression, delimiters) == summary);
    }

    private static Stream<Arguments> provideCustomExpressions() {
        return Stream.of(
            Arguments.of("//s\n1s2s3", 6),
            Arguments.of("//!\n1!4", 5)
        );
    }

    @DisplayName("숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException을 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"abc", "-1", "1,-1", "1:3:b"})
    void no_positive_number(final String expression) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> StringCalculator.calculate(expression, delimiters));
    }
}
