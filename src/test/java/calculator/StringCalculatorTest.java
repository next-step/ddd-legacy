package calculator;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환
 * (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
 *
 *
 * 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다.
 * 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다.
 * 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
 *
 *
 * 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
 */
public class StringCalculatorTest {

    @DisplayName("쉼표 또는 콜론으로 구분된 숫자 문자열을 받아 합을 계산한다.")
    @ParameterizedTest
    @CsvSource(value = {"1:2:3/6", "1,2,3/6", "10,2:3/15", "152/152"}, delimiter = '/')
    void calculate_1(String expression, int expected) {
        //given
        StringCalculator stringCalculator = new StringCalculator();

        //when, then
        assertThat(stringCalculator.calculate(expression)).isEqualTo(expected);
    }

    @DisplayName("null 또는 공백 문자열인 경우 0을 반환한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void calculate_null_or_emptyString(String expression) {
        //given
        StringCalculator stringCalculator = new StringCalculator();
        //when, then
        assertThat(stringCalculator.calculate(expression)).isZero();
    }

    @DisplayName("숫자 이외의 값 또는 음수를 전달하는 경우 에러를 Throw한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1:가", "1,a,3", "-1,2:3"})
    void calculate_with_wrong_value(String expression) {
        //given
        StringCalculator stringCalculator = new StringCalculator();

        //when, then
        assertThatThrownBy(() -> stringCalculator.calculate(expression))
            .isInstanceOf(RuntimeException.class);
    }


    @DisplayName("커스텀 구분자를 사용한 문자열의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3", "//!\n6"})
    void calculate_with_custom_expression_delimiter(String expression) {
        //given
        int expected = 6;
        StringCalculator stringCalculator = new StringCalculator();

        //when,then
        assertThat(stringCalculator.calculate(expression)).isEqualTo(expected);
    }
}
