package calculator;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class StringAdditionCalculatorTest {
    private static StringAdditionCalculator calculator;

    @BeforeAll
    static void setUp() {
        calculator = new StringAdditionCalculator();
    }

    @DisplayName("빈 문자열 또는 null을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void return_0_if_text_is_empty_string(String text) {
        assertThat(calculator.add(text)).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "9"})
    void return_int_if_text_is_number_as_single_char(String text) {
        assertThat(calculator.add(text)).isEqualTo(Integer.parseInt(text));
    }

    @DisplayName("문자열 계산기에 숫자 이외의 값을 전달하는 경우 RuntimeException 예외를 throw 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"l", "일"})
    void throw_RuntimeException_if_text_other_than_number(String text) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add(text));
    }

    @DisplayName("숫자 두개를 컴마(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @MethodSource
    void return_summed_int_if_numbers_separated_by_comma(String text, Integer result) {
        assertThat(calculator.add(text)).isEqualTo(result);
    }

    private static Stream<Arguments> return_summed_int_if_numbers_separated_by_comma() {
        return Stream.of(
            arguments("1,0", 1),
            arguments("1,12", 13)
        );
    }

    @DisplayName("문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "1,-1"})
    void throw_RuntimeException_if_negative_number_included(String text) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add(text));
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @MethodSource
    void return_summed_int_if_using_custom_delimiter(String text, Integer result) {
        assertThat(calculator.add(text)).isEqualTo(result);
    }

    private static Stream<Arguments> return_summed_int_if_using_custom_delimiter() {
        return Stream.of(
            arguments("//;\n1;2;3", 6),
            arguments("//|\n10|22|43", 75)
        );
    }
}
