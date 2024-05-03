package kitchenpos.stringcalculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InputParserTest {

    @DisplayName("기본 구분자로 문자열을 분리할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "1,2:3",
        "4,5:6",
        "7:8,9"
    })
    void parseWithDefaultDelimiters(String input) {
        String[] expected = input.split("[,:]");

        Assertions.assertThat(InputParser.parse(input)).containsExactly(expected);
    }

    @DisplayName("커스텀 구분자로 문자열을 분리할 수 있다.")
    @Test
    void parseWithCustomDelimiter() {
        String input = "//;\n1;2;3";
        String[] expected = {"1", "2", "3"};

        Assertions.assertThat(InputParser.parse(input)).containsExactly(expected);
    }

}
