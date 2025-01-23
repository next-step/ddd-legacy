package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TextParserTest {
    private TextParser<Integer> parser;

    @BeforeEach
    void init() {
        parser = new TextParser<>(new TextConverter<>(s -> new PositiveNumber(new Number(s)).getPrimitiveValue()));
    }

    @ParameterizedTest
    @MethodSource("provideDefaultDelimiterCases")
    @DisplayName(",: 구분자로 문자열을 파싱한다")
    void parse_with_default_delimiter(String text, List<Integer> expected) {
        // when
        List<Integer> result = parser.parse(text);

        // then
        assertThat(result).containsExactlyElementsOf(expected);
    }

    private static Stream<Arguments> provideDefaultDelimiterCases() {
        return Stream.of(
                Arguments.of("1,2,3", List.of(1, 2, 3)),
                Arguments.of("1:2:3", List.of(1, 2, 3)),
                Arguments.of("1,2:3", List.of(1, 2, 3))
        );
    }

    @ParameterizedTest
    @MethodSource("provideCustomDelimiterCases")
    @DisplayName("커스텀 구분자로 문자열을 파싱한다")
    void parse_with_custom_delimiter(String text, List<Integer> expected) {
        // when
        List<Integer> result = parser.parse(text);

        // then
        assertThat(result).containsExactlyElementsOf(expected);
    }

    private static Stream<Arguments> provideCustomDelimiterCases() {
        return Stream.of(
                Arguments.of("//;\n1;2;3", List.of(1, 2, 3)),
                Arguments.of("//*\n1*2*3", List.of(1, 2, 3))
        );
    }
}
