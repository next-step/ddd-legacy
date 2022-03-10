package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class NumberTest {

    @DisplayName("숫자가 아닌 문자가 포함된 value라면 NumberFormatException을 발생시킨다")
    @ParameterizedTest
    @ValueSource(strings = {" ", "1 ", " 1", " 1 ", "1 2", "FF", "+1"})
    void invalidCharacterText(final String value) {
        // given

        // when & then
        assertThatThrownBy(() -> Number.parse(value))
            .isInstanceOf(NumberFormatException.class);
    }

    @DisplayName("value가 음수라면 RuntimeException을 발생시킨다")
    @ParameterizedTest
    @MethodSource(value = "calculator.NumberProvider#oneNegativeNumberProvider")
    void invalidCharacterText(final int negativeValue) {
        // given

        // when & then
        assertThatThrownBy(() -> Number.parse(String.valueOf(negativeValue)))
            .isInstanceOf(RuntimeException.class)
            .isNotInstanceOf(NumberFormatException.class);
    }

    @DisplayName("value를 이용하여 그 값을 가지는 number를 생성한다")
    @ParameterizedTest
    @MethodSource(value = "calculator.NumberProvider#oneZeroOrPositiveNumberProvider")
    void parse(final int value) {
        // given

        // when
        final Number number = Number.parse(String.valueOf(value));

        // then
        assertThat(number.getValue()).isEqualTo(value);
    }

    @DisplayName("두 Number를 더하여 그 값을 포함한 Number를 반환한다")
    @ParameterizedTest
    @MethodSource(value = "calculator.NumberProvider#twoZeroOrPositiveNumberProvider")
    void add(int[] values) {
        // given
        final Number number1 = Number.parse(String.valueOf(values[0]));
        final Number number2 = Number.parse(String.valueOf(values[1]));

        // when
        final Number result = number1.add(number2);

        // then
        assertThat(result.getValue()).isEqualTo(number1.getValue() + number2.getValue());
    }
}