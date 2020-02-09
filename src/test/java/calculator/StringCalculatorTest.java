package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class StringCalculatorTest {

    @DisplayName("null 혹은 빈 문자열은 0을 반환한다.")
    @ParameterizedTest
    @MethodSource("blankStrings")
    void calculateBlankStrings(final String text) {
        assertThat(StringCalculator.calculate(text)).isEqualTo(0);
    }

    private static Stream<String> blankStrings() {
        return Stream.of("", null);
    }

    @DisplayName("구분자 쉼표로 이루어진 문자열을 분리하여 각각의 합을 반환한다.")
    @Test
    void calculateCommaString() {
        String text = "3,5";
        assertThat(StringCalculator.calculate(text)).isEqualTo(8);
    }

    @DisplayName("구분자 콜론으로 이루어진 문자열을 분리하여 각각의 합을 반환한다.")
    @Test
    void calculateSemicolonString() {
        String text = "3:5";
        assertThat(StringCalculator.calculate(text)).isEqualTo(8);
    }

    @DisplayName("구분자 쉼표 및 콜론으로 이루어진 문자열을 분리하여 각각의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"0,3:5", "0:3,5"})
    void calculateDefaultDelimiterString(final String text) {
        assertThat(StringCalculator.calculate(text)).isEqualTo(8);
    }

    @DisplayName("커스텀 구분자로 이루어진 문자열을 분리하여 각각의 합을 반환한다.")
    @Test
    void calculateCustomizedString() {
        String text = "//-\n3-5";
        assertThat(StringCalculator.calculate(text)).isEqualTo(8);
    }

    @DisplayName("숫자가 아닌 문자열이 포함 된 경우 RuntimeException 을 발생시킨다.")
    @Test
    void validateNumericString() {
        String text = "A,5";
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> StringCalculator.calculate(text));
    }

    @DisplayName("값이 음수인 문자열이 포함 된 경우 RuntimeException 을 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1,5", "-1;5", "//s\n-1s5"})
    void validatePositiveNumber(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> StringCalculator.calculate(text));
    }
}
