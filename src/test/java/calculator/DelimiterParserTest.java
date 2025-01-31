package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("구분자 파싱 테스트")
class DelimiterParserTest {
    private final DelimiterParser parser = new DelimiterParser();

    @DisplayName("기본 구분자(쉼표, 콜론)로 분리할 수 있다")
    @ParameterizedTest
    @CsvSource(value = {
            "'1,2,3' | 3",
            "'1:2:3' | 3",
            "'1,2:3' | 3",
            "'1:2,3' | 3"
    }, delimiter = '|')
    void defaultDelimiter(String input, int expectedSize) {
        Numbers result = parser.parse(input);
        Assertions.assertThat(result.size()).isEqualTo(expectedSize);
    }

    @DisplayName("커스텀 구분자를 사용하여 분리할 수 있다")
    @Test
    void customDelimiter() {
        String input = "//;\n1;2;3";
        Numbers result = parser.parse(input);

        Assertions.assertThat(result.size()).isEqualTo(3);
        Assertions.assertThat(result.getValues())
                .extracting(Number::getValue)
                .containsExactly(1, 2, 3);
    }

    @DisplayName("특수문자를 커스텀 구분자로 사용할 수 있다")
    @Test
    void specialCharacterDelimiter() {
        String input = "//.\n1.2.3";
        Numbers result = parser.parse(input);

        Assertions.assertThat(result.size()).isEqualTo(3);
        Assertions.assertThat(result.getValues())
                .extracting(Number::getValue)
                .containsExactly(1, 2, 3);
    }
}
