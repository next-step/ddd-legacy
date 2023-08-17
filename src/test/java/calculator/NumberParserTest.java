package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberParserTest {

    @DisplayName("문자열을 받아 숫자 목록을 반환한다")
    @Test
    void parse() {
        String str = "1,2:3";
        Numbers expected = Numbers.from("1", "2", "3");

        Numbers actual = NumberParser.parse(str);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("커스텀 구분자를 사용하여 분리한 숫자 목록을 반환한다")
    @Test
    void parseWithCustomDelimiter() {
        String str = "//;\n1;2;3";
        Numbers expected = Numbers.from("1", "2", "3");

        Numbers actual = NumberParser.parse(str);

        assertThat(actual).isEqualTo(expected);
    }
}
