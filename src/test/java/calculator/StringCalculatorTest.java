package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringCalculatorTest {

    private StringCalculator calculator;

    @BeforeEach
    void setup() {
        calculator = new StringCalculator();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("빈 문자열 또는 null 전달 시 0을 반환한다.")
    void commaZeroOrNull(final String zeroOrNull) {
        assertThat(calculator.add(zeroOrNull)).isZero();
    }

    @ParameterizedTest
    @MethodSource("singleNumberArguments")
    @DisplayName("하나의 숫자를 문자열로 전달 시 해당 숫자를 반환한다.")
    void singleNumber(final String input, final int expected) {
        assertThat(calculator.add(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("twoNumbersWithComma")
    @DisplayName("두 개 이상의 숫자를 쉼표(,)를 구분자로 가지는 문자열로 전달 시 각 숫자의 합을 반환한다.")
    void commaTwoNumbers(final String input, final int expected) {
        assertThat(calculator.add(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("twoNumbersWithColon")
    @DisplayName("두 개 이상의 숫자를 콜론(:)을 구분자로 가지는 문자열로 전달 시 각 숫자의 합을 반환한다.")
    void colonTwoNumbers(final String input, final int expected) {
        assertThat(calculator.add(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("customSeparatorArguments")
    @DisplayName("'//'와 '\n' 사이에 커스텀 구분자를 지정할 수 있다.")
    void customSeparator(final String input, final int expected) {
        assertThat(calculator.add(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "-1",
        "-4,3",
        "-1:3",
        "//.\n-5.6",
    })
    @DisplayName("음수를 문자열로 전달 시 RuntimeException 던지며 예외처리한다.")
    void negativeNumbers(final String input) {
        assertThatThrownBy(() -> calculator.add(input))
            .isInstanceOf(RuntimeException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "a",
        "b,c",
        "-b:z",
        "//.\n-가.나",
    })
    @DisplayName("숫자가 아닌 문자를 전달 시 RuntimeException 던지며 예외처리한다.")
    void notNumbers(final String input) {
        assertThatThrownBy(() -> calculator.add(input))
            .isInstanceOf(RuntimeException.class);
    }

    private static List<Arguments> singleNumberArguments() {
        return List.of(
            Arguments.of("1", 1),
            Arguments.of("10", 10)
        );
    }

    private static List<Arguments> twoNumbersWithComma() {
        return List.of(
            Arguments.of("1,2", 3),
            Arguments.of("4,5", 9),
            Arguments.of("4,5,6", 15),
            Arguments.of("4,5,6,7", 22),
            Arguments.of("100,23,1,2,3", 129),
            Arguments.of("999,1,15,5,100,6", 1126)
        );
    }

    private static List<Arguments> twoNumbersWithColon() {
        return List.of(
            Arguments.of("1:2", 3),
            Arguments.of("4:5", 9),
            Arguments.of("4:5:6", 15),
            Arguments.of("4:5:6:7", 22),
            Arguments.of("100:23:1:2:3", 129),
            Arguments.of("999:1:15:5:100:6", 1126)
        );
    }

    private static List<Arguments> customSeparatorArguments() {
        return List.of(
            Arguments.of("//;\n1;2", 3),
            Arguments.of("//.\n5.6", 11)
        );
    }
}
