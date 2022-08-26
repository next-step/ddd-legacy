package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringNumberParserTest {

    private StringNumberParser stringNumberParser;

    @BeforeEach
    void setUp() {
        stringNumberParser = new StringNumberParser();
    }

    @DisplayName("기본적으로 ',' 와 ':' 구분자를 지정한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2,3", "1:2:3", "1,2:3"})
    void success(String actual) {
        assertThat(stringNumberParser.toPositiveNumbers(actual))
                .hasSize(3);
    }

    @DisplayName("'//'와 '\n' 문자 사이에 커스텀 구분자를 지정할 수 있다. ")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3",})
    void customDelimiter(String actual) {
        assertThat(stringNumberParser.toPositiveNumbers(actual))
                .hasSize(3);
    }
}
