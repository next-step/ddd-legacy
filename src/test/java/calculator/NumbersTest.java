package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

@DisplayName("Numbers 테스트")
class NumbersTest {

    @DisplayName("문자열 배열을 숫자로 변환하여 합을 계산한다")
    @ParameterizedTest
    @MethodSource("provideSumTestCases")
    void sum(String[] input, int expected) {
        Numbers numbers = Numbers.from(input);
        Assertions.assertThat(numbers.sum()).isEqualTo(expected);
    }

    private static Stream<Arguments> provideSumTestCases() {
        return Stream.of(
                Arguments.of(new String[]{"1"}, 1),
                Arguments.of(new String[]{"1", "2"}, 3),
                Arguments.of(new String[]{"1", "2", "3"}, 6),
                Arguments.of(new String[]{"10", "20", "30"}, 60)
        );
    }

    @DisplayName("숫자가 아닌 값이 포함된 경우 IllegalArgumentException 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"a", "abc", "1a", "a1"})
    void createWithNonNumericInput(String nonNumeric) {
        String[] input = {"1", nonNumeric, "3"};

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Numbers.from(input))
                .withMessage("숫자가 아닌 값이 포함되어 있습니다.");
    }

    @DisplayName("음수가 포함된 경우 RuntimeException 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-10", "-100"})
    void createWithNegativeNumber(String negative) {
        String[] input = {"1", negative, "3"};

        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> Numbers.from(input))
                .withMessage("음수는 허용되지 않습니다.");
    }
}
