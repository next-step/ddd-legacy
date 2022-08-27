package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import common.numeric.NonNegativeNumber;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringExpressionTest {

    private static Stream<Arguments> provideParseTestArguments() {
        return Stream.of(
            Arguments.of("1", collectToNonNegativeList(1)),
            Arguments.of("1,2", collectToNonNegativeList(1, 2)),
            Arguments.of("1,2:3", collectToNonNegativeList(1, 2, 3)),
            Arguments.of("//;\n1;2;3", collectToNonNegativeList(1, 2, 3))
        );
    }

    private static List<NonNegativeNumber> collectToNonNegativeList(final Integer... numbers) {
        return Arrays.stream(numbers)
            .map(NonNegativeNumber::new)
            .collect(Collectors.toUnmodifiableList());
    }

    @DisplayName("null 또는 빈 스트링이 입력되는 경우 RuntimeException 이 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void createFailTest(final String given) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> StringExpression.of(given));
    }

    @DisplayName("문자열 계산식을 파싱할 수 있다.")
    @ParameterizedTest
    @MethodSource("provideParseTestArguments")
    void parseTest(final String given, final List<NonNegativeNumber> expected) {
        final StringExpression expression = StringExpression.of(given);
        assertThat(expression.parse())
            .containsExactlyElementsOf(expected);
    }
}