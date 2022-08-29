package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class AdditionExpressionTest {
    @DisplayName("null로 생성하면 안 된다.")
    @Test
    void nullValue() {
        assertThatIllegalArgumentException().isThrownBy(() -> new AdditionExpression(null));
    }

    @DisplayName("콤마로 구분된 여러 숫자를 입력하면, 숫자의 합을 반환한다.")
    @Test
    void multipleNumberWithCommaOrColon() {
        final var expression = new AdditionExpression("1,2:3");
        assertThat(expression.splitTokensByDelimiter()).containsExactly("1", "2", "3");
    }

    @DisplayName("//와 \\n 사이에 위치하는 문자를 커스텀 구분자로 사용")
    @Test
    void customDelimiterPart() {
        final var expression = new AdditionExpression("//;\n78;9");
        assertThat(expression.splitTokensByDelimiter()).containsExactly("78", "9");
    }

    @DisplayName("숫자 부분이 비어 있으면 빈 배열을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "//;\n"
    })
    void emptyTokensPart(String input) {
        final var expression = new AdditionExpression(input);
        assertThat(expression.splitTokensByDelimiter()).isEmpty();
    }
}
