package calculator.strategy;

import calculator.Numbers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class NumbersSplitStrategyTest {
    static Stream<Arguments> strategyProvider() {
        NumbersSplitStrategy zeroNumbersStrategy = new ZeroNumbersSplitStrategy();
        NumbersSplitStrategy basicStrategy = new BasicSplitStrategy();
        NumbersSplitStrategy customStrategy = new CustomSplitStrategy();

        return Stream.of(
                Arguments.of(zeroNumbersStrategy, null, Numbers.ZERO_NUMBERS),
                Arguments.of(zeroNumbersStrategy, "", Numbers.ZERO_NUMBERS),
                Arguments.of(basicStrategy, "1,2:3,4", new Numbers("1", "2", "3", "4")),
                Arguments.of(customStrategy, "//;\n1;2;3;4", new Numbers("1", "2", "3", "4"))
        );
    }

    @DisplayName("각 구분자 전략에 맞게 추출한 숫자들을 반환한다.")
    @ParameterizedTest
    @MethodSource("strategyProvider")
    void testExtract(NumbersSplitStrategy strategy, String input, Numbers expected) {
        Numbers result = strategy.extract(input);
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("각 전략별로 패턴이 일치하면 참을 반환한다.")
    @ParameterizedTest
    @MethodSource("strategyProvider")
    void testIsMatchPattern(NumbersSplitStrategy strategy, String input) {
        boolean result = strategy.isMatchPattern(input);
        assertThat(result).isTrue();
    }
}
