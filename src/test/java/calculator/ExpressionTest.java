package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ExpressionTest {

    private static Stream<Arguments> provideParseTestArguments() {
        return Stream.of(
            Arguments.of("1", List.of(1)),
            Arguments.of("1,2", List.of(1, 2)),
            Arguments.of("1,2:3", List.of(1, 2, 3)),
            Arguments.of("//;\n1;2;3", List.of(1, 2, 3))
        );
    }

    @DisplayName("null 또는 빈 스트링이 입력되는 경우 RuntimeException 이 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void createFailTest(final String given) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> Expression.of(given));
    }

    @DisplayName("문자열 계산식을 파싱할 수 있다.")
    @ParameterizedTest
    @MethodSource("provideParseTestArguments")
    void parseTest(final String given, final List<Integer> expected) {
        final Expression expression = Expression.of(given);
        assertThat(expression.parse())
            .containsExactlyElementsOf(expected);
    }
}