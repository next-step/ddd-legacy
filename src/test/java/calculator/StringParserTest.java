package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StringParserTest {
    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 파싱할 수 있다.")
    @Test
    void customDelimiter() {
        String text = "//;\n1;2;3";
        StringParser stringParser = StringParser.of(text);
        assertAll(
                () -> assertThat(stringParser.getNumberText())
                        .isEqualTo("1;2;3"),
                () -> assertThat(stringParser.getDelimiter())
                        .isEqualTo(",|\\:|\\;")
        );
    }
}
