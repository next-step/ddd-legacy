package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StringSplitterTest {

    @DisplayName("구분자가 없을 때, 입력값을 그대로 반환한다.")
    @Test
    void withoutDelimiter() {
        String input = "123";
        assertThat(StringSplitter.extractNumberStringByDelimiter(input))
                .containsOnly(input);
    }

    @DisplayName("문자열에 ',|:' 이 포함된 경우 String타입 숫자를 반환한다.")
    @Test
    void withDelimiterRegex() {
        String[] expect = {"1", "2", "3", "4"};
        assertThat(StringSplitter.extractNumberStringByDelimiter("1,2,3:4"))
                .containsAnyOf(expect);
    }

    @DisplayName("문자열에 CustomDelimiter가 포함된 경우")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3;4", "//a\n1a2a3a4", "//!\n1!2!3!4"})
    void withCustomedDelimiterRegex(final String input) {
        String[] expect = {"1", "2", "3", "4"};

        assertThat(StringSplitter.extractNumberStringByDelimiter(input))
                .containsOnly(expect);
    }
}
