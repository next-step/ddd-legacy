package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class AdditionExpressionTest {
    @DisplayName("null로 생성하면 안 된다.")
    @Test
    void nullValue() {
        assertThatIllegalArgumentException().isThrownBy(() -> new AdditionExpression(null));
    }

    @DisplayName("//와 \\n 사이에 위치하는 문자를 커스텀 구분자로 사용")
    @ParameterizedTest(name = "\"{0}\"를 입력하면, 커스텀 구분자는 {1}이다.")
    @CsvSource(value = {
            "'//;\n1;2'|;",
            "'//#\n'|#"
    }, delimiter = '|')
    void customDelimiterPart(String input, String customDelimiterPart) {
        AdditionExpression expression = new AdditionExpression(input);
        assertThat(expression.getCustomDelimiter()).isEqualTo(customDelimiterPart);
    }

    @DisplayName("커스텀 구분 표현 다음 부분이 숫자 부분이다.")
    @ParameterizedTest(name = "\"{0}\"를 입력하면, 숫자 부분은 {1}이다.")
    @CsvSource(value = {
            "'//;\n1;2'|'1;2'",
            "'//#\n'|''"
    }, delimiter = '|')
    void tokensPart(String input, String tokensPart) {
        AdditionExpression expression = new AdditionExpression(input);
        assertThat(expression.getTokens()).isEqualTo(tokensPart);
    }
}
