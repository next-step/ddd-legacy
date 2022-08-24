package calculator.delimiter;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CustomDelimiterTest {


    @DisplayName("'\\'와 '\n'의 사이에 위치하는 문자를 기준으로 문자열을 분리한다")
    @ParameterizedTest
    @MethodSource("provideCustomExpressions")
    void split(final String expression, final List<String> splitExpression) {
        final Delimiter customDelimiter = new CustomDelimiter();
        final List<String> result = customDelimiter.split(Arrays.asList(expression));

        assertThat(result).isEqualTo(splitExpression);
    }

    private static Stream<Arguments> provideCustomExpressions() {
        return Stream.of(
            Arguments.of("//;\n1;2;3", Arrays.asList("1", "2", "3")),
            Arguments.of("//v\n1v2v4", Arrays.asList("1", "2", "4")),
            Arguments.of("//v\n1v2:4", Arrays.asList("1", "2:4"))
        );
    }
}
