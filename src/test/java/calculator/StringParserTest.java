package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class StringParserTest {

    @DisplayName("콤마가 포함된 문자열을 파싱할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2,3"})
    void comma(final String text) {
        assertThat(StringParser.splitNumbers(text)).containsExactly("1", "2", "3");
    }

    @DisplayName("콜론이 포함된 문자열을 파싱할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1:2:3"})
    void colon(final String text) {
        assertThat(StringParser.splitNumbers(text)).containsExactly("1", "2", "3");
    }

    @DisplayName("콤마와 콜론이 포함된 문자열을 파싱할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3", "1:2,3"})
    void colonsAndCommas(final String text) {
        assertThat(StringParser.splitNumbers(text)).containsExactly("1", "2", "3");
    }

    @DisplayName("//와 \\n 문자 사이의 커스텀 구분자를 통해 문자열을 파싱할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(StringParser.splitNumbers(text)).containsExactly("1", "2", "3");
    }

    @DisplayName("콤마, 콜론과 커스텀 구분자가 포함된 문자열을 파싱할 수 있다.")
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            //;\\n1,2:3;4 | 1,2,3,4
        """)
    @ValueSource(strings = {"//;\\n1,2:3;4"})
    void commaAndColonAndCustomDelimiter(final String text) {
        assertThat(StringParser.splitNumbers(text)).containsExactly("1", "2", "3", "4");
    }
}
