package calculator;

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

class StringCalculatorTest {
    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void add_when_text_is_empty_or_null(final String text) {
        assertThat(calculator.add(text)).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = { "1", "11" })
    void add_when_text_is_one_number(final String text) {
        assertThat(calculator.add(text)).isSameAs(Integer.parseInt(text));
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @MethodSource("add_when_text_is_two_numbers_delimited_by_comma_cases")
    @ParameterizedTest
    void add_when_text_is_two_numbers_delimited_by_comma(final int firstNumber,
                                                         final int secondNumber) {
        assertThat(calculator.add(firstNumber + "," + secondNumber))
            .isSameAs(firstNumber + secondNumber);
    }

    private static Stream<Arguments> add_when_text_is_two_numbers_delimited_by_comma_cases() {
        return Stream.of(Arguments.of(1, 2),
                         Arguments.of(11, 12));
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @MethodSource("add_when_text_is_numbers_delimited_by_comma_or_colon_cases")
    @ParameterizedTest
    void add_when_text_is_numbers_delimited_by_comma_or_colon(final String text,
                                                              final int expected) {
        assertThat(calculator.add(text)).isSameAs(expected);
    }

    private static Stream<Arguments> add_when_text_is_numbers_delimited_by_comma_or_colon_cases() {
        return Stream.of(Arguments.of("1,2:3", 6),
                         Arguments.of("10,20:30,40", 100));
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = { "//;\n1;2:3,4" })
    void customDelimiter(final String text) {
        assertThat(calculator.add(text)).isSameAs(10);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add("1,-2"));
    }
}
