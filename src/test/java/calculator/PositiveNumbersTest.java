package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PositiveNumbersTest {
    @ParameterizedTest
    @MethodSource("provideValues")
    @DisplayName("전달한 숫자들과 default result 를 모두 더함")
    void sumValuesAndDefaultResult(List<Integer> values, int defaultResult, int expected) {
        // given
        PositiveNumbers numbers = new PositiveNumbers(values);

        // when & then
        assertThat(numbers.sum(defaultResult)).isEqualTo(expected);
    }

    private static Stream provideValues() {
        return Stream.of(
                Arguments.of(Arrays.asList(1, 2, 3), 0, 6),
                Arguments.of(Arrays.asList(1, 2, 3), 2, 8)
        );
    }
}
