package calculator.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StringCalculatorInputParserTest {

    private StringCalculatorInputParser sut;
    private StringCalculatorDelimiters delimiters;

    @BeforeEach
    void setUp() {
        delimiters = StringCalculatorDelimiters.create();
        sut = new StringCalculatorInputParser(delimiters);
    }

    @DisplayName("기본 구분자로 문자열을 분리해야 한다.")
    @ParameterizedTest
    @MethodSource("provideDefaultDelimiterTestCases")
    void parse_shouldSplitByDefaultDelimiters(String input, String[] expected) {
        assertThat(sut.parse(input)).containsExactly(expected);
    }

    @DisplayName("커스텀 구분자로 문자열을 분리해야 한다.")
    @ParameterizedTest
    @MethodSource("provideCustomDelimiterTestCases")
    void parse_shouldSplitByCustomDelimiter(String input, String[] expected) {
        assertThat(sut.parse(input)).containsExactly(expected);
    }

    private static Stream<Arguments> provideDefaultDelimiterTestCases() {
        return Stream.of(
                Arguments.of("1,2,3", new String[]{"1", "2", "3"}),
                Arguments.of("4:5:6", new String[]{"4", "5", "6"}),
                Arguments.of("7,8:9", new String[]{"7", "8", "9"})
        );
    }

    private static Stream<Arguments> provideCustomDelimiterTestCases() {
        return Stream.of(
                Arguments.of("//;\n1;2;3", new String[]{"1", "2", "3"}),
                Arguments.of("//#\n4#5#6", new String[]{"4", "5", "6"}),
                Arguments.of("//@\n7@8@9", new String[]{"7", "8", "9"})
        );
    }
}