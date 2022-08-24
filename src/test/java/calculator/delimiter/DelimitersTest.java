package calculator.delimiter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DelimitersTest {

    @DisplayName("Delimiters는 Delimeter가 꼭 필요하다")
    @ParameterizedTest
    @NullAndEmptySource
    void constructor(final List<Delimiter> delimiterList) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Delimiters(delimiterList));
    }

    @DisplayName("하나의 delimiter는 하나의 문자만 나눌 수 있다.")
    @ParameterizedTest
    @MethodSource("provideOneDelimiter")
    void separate_by_one_delimiter(final String expression, final List<String> splitExpression, final Delimiter delimiter) {
        final Delimiters delimiters = new Delimiters(Arrays.asList(delimiter));
        final List<String> result = delimiters.split(expression);

        assertThat(result).isEqualTo(splitExpression);
    }

    private static Stream<Arguments> provideOneDelimiter() {
        return Stream.of(
            Arguments.of("1,2,3:4", Arrays.asList("1", "2", "3:4"), new CommaDelimiter()),
            Arguments.of("1,2:3:4", Arrays.asList("1,2", "3", "4"), new ColonDelimiter()),
            Arguments.of("//a\n1a2;3", Arrays.asList("1", "2;3"), new CustomDelimiter())
        );
    }

    @DisplayName("혼합된 delimiter는 여러개의 문자를 나눌 수 있다.")
    @ParameterizedTest
    @MethodSource("provideMultipleDelimiter")
    void separate_by_multiple_delimiter(final String expression, final List<String> splitExpression, final List<Delimiter> delimiterList) {
        final Delimiters delimiters = new Delimiters(delimiterList);
        final List<String> result = delimiters.split(expression);

        assertThat(result).isEqualTo(splitExpression);
    }

    private static Stream<Arguments> provideMultipleDelimiter() {
        return Stream.of(
            Arguments.of("//a\n1:2,3a4", Arrays.asList("//a\n1", "2", "3a4"), Arrays.asList(new ColonDelimiter(), new CommaDelimiter())),
            Arguments.of("//a\n1:2a3", Arrays.asList("1", "2a3"), Arrays.asList(new ColonDelimiter(), new CustomDelimiter())),
            Arguments.of("//a\n1:a,3", Arrays.asList("1:", "3"), Arrays.asList(new CommaDelimiter(), new CustomDelimiter())),
            Arguments.of("//!\n1:2,3:4,5", Arrays.asList("1", "2", "3", "4", "5"), Arrays.asList(new ColonDelimiter(), new CommaDelimiter(), new CustomDelimiter()))
        );
    }
}
