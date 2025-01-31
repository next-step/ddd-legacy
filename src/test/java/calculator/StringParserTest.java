package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StringParserTest {

    private void assertParsedCorrectly(String text, String expected) {
        assertThat(StringParser.splitNumbers(text)).containsExactly(expected.split(","));
    }

    @DisplayName("콤마가 포함된 문자열을 파싱할 수 있다.")
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
        1,2 | 1,2
        1,2,3 | 1,2,3
    """)
    void comma(final String text, final String expected) {
        assertParsedCorrectly(text, expected);
    }

    @DisplayName("콜론이 포함된 문자열을 파싱할 수 있다.")
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
        1:2 | 1,2
        1:2:3 | 1,2,3
    """)
    void colon(final String text, final String expected) {
        assertParsedCorrectly(text, expected);
    }

    @DisplayName("콤마와 콜론이 포함된 문자열을 파싱할 수 있다.")
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
        1,2:3 | 1,2,3
        1:2,3 | 1,2,3
        1,2:3,4 | 1,2,3,4
        1:2,3:4 | 1,2,3,4
    """)
    void colonsAndCommas(final String text, final String expected) {
        assertParsedCorrectly(text, expected);
    }

    @DisplayName("//와 \\n 문자 사이의 커스텀 구분자를 통해 문자열을 파싱할 수 있다.")
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
        //;\\n1;2;3 | 1,2,3
    """)
    void customDelimiter(final String text, final String expected) {
        assertParsedCorrectly(text, expected);
    }

    @DisplayName("콤마, 콜론과 커스텀 구분자가 포함된 문자열을 파싱할 수 있다.")
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
        //;\\n1,2:3;4 | 1,2,3,4
    """)
    void commaAndColonAndCustomDelimiter(final String text, final String expected) {
        assertParsedCorrectly(text, expected);
    }
}
