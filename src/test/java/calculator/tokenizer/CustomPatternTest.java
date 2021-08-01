package calculator.tokenizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CustomPattern 테스트")
public class CustomPatternTest {

    @DisplayName("//와 \n 사이의 문자가 존재한다")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void find(final String text) {
        CustomPattern pattern = new CustomPattern(text);
        assertThat(pattern.find()).isTrue();
    }

    @DisplayName("//와 \n 사이의 문자를 구분자로 그룹을 확인한다")
    @ParameterizedTest(name = "{0} 문자열 분리")
    @ValueSource(strings = {"//;\n1;2;3"})
    void group(final String text) {
        CustomPattern pattern = new CustomPattern(text);
        if (pattern.find()) {
            assertThat(pattern.group(1)).isEqualTo(";");
            assertThat(pattern.group(2)).isEqualTo("1;2;3");
        }
    }
}
