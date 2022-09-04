package calculate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환 (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
 * 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다. 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
 * 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
 */
class StringCalculatorTest {
    private StringCalculator stringCalculator;

    @BeforeEach
    void setup() {
        stringCalculator = new StringCalculator();
    }

    @ParameterizedTest
    @DisplayName("문자열이 null이거나 비어있으면 0을 반환한다.")
    @NullAndEmptySource
    void calculate_null_and_empty_string(String text) {
        assertThat(stringCalculator.calculate(text)).isEqualTo(0);
    }

    @ParameterizedTest
    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 문자를 숫자로 반환한다.")
    @ValueSource(strings = {"1", "2", "3", "4", "5"})
    void calculate_one_string_number(String text) {
        assertThat(stringCalculator.calculate(text)).isEqualTo(Integer.parseInt(text));
    }

    @Test
    @DisplayName("문자열에 음수를 입력한 경우 예외가 발생한다.")
    void calculate_minus_number() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> stringCalculator.calculate("1;-1;2"));
    }

    @Test
    @DisplayName("문자열에 숫자가 아닌 다른 문자를 입력한 경우 예외가 발생한다.")
    void calculate_wrong_number() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> stringCalculator.calculate("1;s;2"));
    }

    @ParameterizedTest
    @DisplayName("구분자를 쉼표로 숫자의 합을 구할 수 있다.")
    @ValueSource(strings = {"1,2,3"})
    void calculate_comma(String text) {
        assertThat(stringCalculator.calculate(text)).isEqualTo(6);
    }

    @ParameterizedTest
    @DisplayName("구분자를 콜론으로 숫자의 합을 구할 수 있다.")
    @ValueSource(strings = {"1:2:3"})
    void calculate_colon(String text) {
        assertThat(stringCalculator.calculate(text)).isEqualTo(6);
    }

    @ParameterizedTest
    @DisplayName("구분자를 콜론 또는 쉼표로 숫자의 합을 구할 수 있다.")
    @MethodSource("calculatorParametersProvider")
    void calculate_colon_and_comma(String text, int result) {
        assertThat(stringCalculator.calculate(text)).isEqualTo(result);
    }

    @ParameterizedTest
    @DisplayName("커스텀 구분자를 지정하여 숫자의 합을 구할 수 있다.")
    @MethodSource("customCalculatorParametersProvider")
    void calculate_custom(String text, int result) {
        assertThat(stringCalculator.calculate(text)).isEqualTo(result);
    }

    static Stream<Arguments> calculatorParametersProvider() {
        return Stream.of(
                Arguments.arguments("1,2:3", 6),
                Arguments.arguments("2:3,4,5", 14)
        );
    }

    static Stream<Arguments> customCalculatorParametersProvider() {
        return Stream.of(
                Arguments.arguments("//;\n1;2;3", 6),
                Arguments.arguments("//@\n1@2@3", 6),
                Arguments.arguments("//#\n2#3#4#5", 14)
        );
    }

}