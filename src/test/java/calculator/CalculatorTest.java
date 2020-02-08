package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("null 또는 빈 문자열을 전달하는 경우 0을 반환")
    void shouldBeZeroWhenCalculateEmptyString() {
        assertThat(calculator.calculate(null)).isEqualTo(Calculator.DEFAULT_RESULT);
        assertThat(calculator.calculate("")).isEqualTo(Calculator.DEFAULT_RESULT);
    }

    @Test
    @DisplayName("하나의 숫자만 있는 문자열을 전달하는 경우 해당 숫자를 반환")
    void calculateStringWithOneNumber() {
        final String text = "1";
        assertThat(calculator.calculate(text)).isEqualTo(Integer.parseInt(text));
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithDefaultDelimiters")
    @DisplayName(", 또는 :을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환")
    void calculateStringWithDefaultDelimiters(String value, int expected) {
        assertThat(calculator.calculate(value)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideValuesWithDefaultDelimiters() {
        return Stream.of(
                Arguments.of("1,2,3", 6),
                Arguments.of("1:2", 3),
                Arguments.of("1,2:3:4,5", 15)
        );
    }

    @ParameterizedTest
    @MethodSource("provideValuesWithCustomDelimiter")
    @DisplayName("문자열 앞부분의 “//”와 “\\n” 사이에 위치하는 문자를 커스텀 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환")
    void calculateStringWithCustomDelimiters(String value, int expected) {
        assertThat(calculator.calculate(value)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideValuesWithCustomDelimiter() {
        return Stream.of(
                Arguments.of("//#\\n1", 1),
                Arguments.of("//;\\n1;2;3", 6),
                Arguments.of("//#\\n1#2#3#4", 10)
        );
    }


    @ParameterizedTest
    @MethodSource("provideInvalidValues")
    @DisplayName("숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외 던짐")
    void shouldThrowExceptionWhenCanNotParseNumberOrNegativeNumber(String value) {
        assertThatThrownBy(() -> calculator.calculate(value))
                .isInstanceOf(RuntimeException.class);
    }

    private static Stream<Arguments> provideInvalidValues() {
        return Stream.of(
                Arguments.of("1,b,3"),
                Arguments.of("c:5"),
                Arguments.of("6,d:8:9,f"),
                Arguments.of("//@\\na@5"),
                Arguments.of("-1"),
                Arguments.of("-1:2")
        );
    }
}
