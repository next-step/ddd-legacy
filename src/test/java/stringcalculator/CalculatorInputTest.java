package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorInputTest {
    private CalculatorInput calculatorInput;

    @DisplayName(value = "기본 구분자로 쉼표(,)와 콜론(:)을 사용한다.")
    @ParameterizedTest
    @MethodSource("provideDefaultDelimiterTestSource")
    void defaultDelimiter(final String text, final String[] expected) {
        calculatorInput = new CalculatorInput(text);
        assertThat(calculatorInput.parse()).containsExactly(expected);
    }

    @DisplayName(value = "커스텀 구분자를 사용할 수 있다.")
    @ParameterizedTest
    @MethodSource("provideCustomDelimiterTestSource")
    void customDelimiter(final String text, final String[] expected) {
        calculatorInput = new CalculatorInput(text);
        assertThat(calculatorInput.parse()).containsExactly(expected);
    }

    @DisplayName(value = "여러 구분자를 함께 사용할 수 있다.")
    @ParameterizedTest
    @MethodSource("provideMixedDelimiterTestSource")
    void mixedDelimiter(final String text, final String[] expected) {
        calculatorInput = new CalculatorInput(text);
        assertThat(calculatorInput.parse()).containsExactly(expected);
    }


    private static Stream<Arguments> provideDefaultDelimiterTestSource() {
        return Stream.of(
                Arguments.of("1,2", new String[] {"1", "2"}),
                Arguments.of("1:2", new String[] {"1", "2"})
        );
    }

    private static Stream<Arguments> provideCustomDelimiterTestSource() {
        return Stream.of(
                Arguments.of("//$\n1$2$3", new String[] {"1", "2", "3"}),
                Arguments.of("//-\n1-2-3", new String[] {"1", "2", "3"})
        );
    }

    private static Stream<Arguments> provideMixedDelimiterTestSource() {
        return Stream.of(
                Arguments.of("1,2:3", new String[] {"1", "2", "3"}),
                Arguments.of("//$\n1,2$3", new String[] {"1", "2", "3"}),
                Arguments.of("//$\n1,2$3:4", new String[] {"1", "2", "3", "4"})
                );
    }
}
