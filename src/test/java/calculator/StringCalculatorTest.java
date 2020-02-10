package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class StringCalculatorTest {
    private final StringCalculator calculator = new StringCalculator();

    private final static Stream<Arguments> customRegexDelimiterArguments() {
        return Stream.of(
                arguments("//$\n1,2$3", new String[]{"1", "2", "3"}),
                arguments("//--\n1,2--3", new String[]{"1", "2", "3"})
        );
    }

    private final static Stream<Arguments> throwWithNonNumericExceptionArguments() {
        return Stream.of(
                arguments(new String[]{"-1", "2", "3"}),
                arguments(new String[]{"1", "q", "3"})
        );
    }

    private final static Stream<Arguments> calculateValuesArguments() {
        return Stream.of(
                arguments("//-\n1,2,3-4:5", 15),
                arguments("", 0),
                arguments("//-\n0,0,1:2", 3),
                arguments("//-\n1,2-3", 6)
        );
    }

    @Test
    @DisplayName("구분자로 숫자를 구분한다.")
    void findDefaultDelimiter() {
        assertThat(calculator.splitInputs("1,2,3")).containsExactly("1", "2", "3");
    }

    @ParameterizedTest
    @MethodSource("customRegexDelimiterArguments")
    @DisplayName("지정 구분자를 포함한 구분자 정규식을 찾는다")
    void findCustomRegexDelimiterArguments(String input, String[] output) {
        assertThat(calculator.splitInputs(input)).containsExactly(output);
    }

    @ParameterizedTest
    @MethodSource("throwWithNonNumericExceptionArguments")
    @DisplayName("숫자 이외의 값 또는 음수는 RuntimeException을 발생한다.")
    void throwWithNonNumericException(String input) {
        System.out.println(input);
    }

    @ParameterizedTest
    @MethodSource("calculateValuesArguments")
    @DisplayName("문자열 계산기의 결과값을 반환한다.")
    void calculateValues(String string, int result) {
        assertThat(calculator.calculate(string)).isEqualTo(result);
    }
}