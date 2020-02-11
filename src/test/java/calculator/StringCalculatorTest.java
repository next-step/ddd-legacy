package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class StringCalculatorTest {
    private final StringCalculator calculator = new StringCalculator();

    private static final Stream<Arguments> calculateValuesArguments() {
        return Stream.of(
                arguments("//-\n1,2,3-4:5", 15),
                arguments("", 0),
                arguments("//-\n0,0,1:2", 3),
                arguments("//-\n1,2-3", 6)
        );
    }

    @ParameterizedTest
    @MethodSource("calculateValuesArguments")
    @DisplayName("문자열 계산기의 결과값을 반환한다.")
    void calculateValues(String string, int result) {
        assertThat(calculator.calculate(string)).isEqualTo(result);
    }
}
