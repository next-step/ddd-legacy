package calculator.delimiter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CommaDelimiterTest {

    private static Stream<Arguments> provideCommaExpressions() {
        return Stream.of(
            Arguments.of("1:2", Arrays.asList("1:2")),
            Arguments.of("1,2,3", Arrays.asList("1", "2", "3")),
            Arguments.of("1,2:3", Arrays.asList("1", "2:3"))
        );
    }

    @DisplayName(",를 기준으로 문자열을 분리한다")
    @ParameterizedTest
    @MethodSource("provideCommaExpressions")
    void split(final String expression, final List<String> splitExpression) {
        final Delimiter commaDelimiter = new CommaDelimiter();
        final List<String> result = commaDelimiter.split(Arrays.asList(expression));

        assertThat(result).isEqualTo(splitExpression);
    }
}
