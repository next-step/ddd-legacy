package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class StringOperandsTest {
    @DisplayName(value = "빈 문자열을 입력할 경우 true를 반환해야 한다.")
    @ParameterizedTest
    @MethodSource("provideEmpty")
    void empty(final String operandsText, final String delimiter) {
        assertThat(StringOperands.of(operandsText, delimiter).isEmpty()).isTrue();
    }

    private static Stream<Arguments> provideEmpty() {
        return Stream.of(
                Arguments.of("", ",|:")
        );
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 false를 반환해야 한다.")
    @ParameterizedTest
    @CsvSource(value = {"1 ,|:"}, delimiter = ' ')
    void oneNumber(final String operandsText, final String delimiter) {
        assertThat(StringOperands.of(operandsText, delimiter).isEmpty()).isFalse();
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 false를 반환해야 한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,2 ,|:"}, delimiter = ' ')
    void twoNumbers(final String operandsText, final String delimiter) {
        assertThat(StringOperands.of(operandsText, delimiter).isEmpty()).isFalse();
    }
}
