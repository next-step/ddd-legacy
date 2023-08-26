package calculator;

import calculator.application.DelimiterParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DelimiterParserTest {
    @DisplayName(value = "커스텀 구분자를 기준으로 숫자를 분리할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(DelimiterParser.splitText((text))).isEqualTo(new String[]{"1", "2", "3"});
    }
}
