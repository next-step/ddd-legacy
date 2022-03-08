package caculator;

import static org.assertj.core.api.Assertions.assertThat;

import caculator.domain.Calculator;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CalculatorTest {

    @ParameterizedTest(name = "문자열 덧셈 {index} [{arguments}]")
    @DisplayName("문자열을 분리하여 덧셈")
    @MethodSource
    void add(String stringNumbers, int expected) {
        //when
        int actual = Calculator.add(stringNumbers);

        //then
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> add() {
        return Stream.of(
            Arguments.of("", 0),
            Arguments.of(null, 0),
            Arguments.of("1,2:3", 6),
            Arguments.of("//;\n1;2;3", 6)
        );
    }
}
