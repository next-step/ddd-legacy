package calculator.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class InputParserTest {

    private InputParser parser;

    @BeforeEach
    void setUp() {
        parser = new InputParser();
    }

    @DisplayName("기본 구분자로 문자열을 분리해야 한다.")
    @ParameterizedTest
    @MethodSource("provideDefaultDelimiterTestCases")
    void parse_shouldSplitByDefaultDelimiters(String input, String[] expected) {
        assertThat(parser.parse(input)).containsExactly(expected);
    }

    @DisplayName("커스텀 구분자로 문자열을 분리해야 한다.")
    @ParameterizedTest
    @MethodSource("provideCustomDelimiterTestCases")
    void parse_shouldSplitByCustomDelimiter(String input, String[] expected) {
        assertThat(parser.parse(input)).containsExactly(expected);
    }

    private static Stream<Object[]> provideDefaultDelimiterTestCases() {
        return Stream.of(
                new Object[]{"1,2,3", new String[]{"1", "2", "3"}},
                new Object[]{"4:5:6", new String[]{"4", "5", "6"}},
                new Object[]{"7,8:9", new String[]{"7", "8", "9"}}
        );
    }

    private static Stream<Object[]> provideCustomDelimiterTestCases() {
        return Stream.of(
                new Object[]{"//;\n1;2;3", new String[]{"1", "2", "3"}},
                new Object[]{"//#\n4#5#6", new String[]{"4", "5", "6"}},
                new Object[]{"//@\n7@8@9", new String[]{"7", "8", "9"}}
        );
    }
}