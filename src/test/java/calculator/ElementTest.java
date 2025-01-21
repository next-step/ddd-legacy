package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class ElementTest {
    @DisplayName("한 자리수 숫자만 허용")
    @ParameterizedTest
    @CsvSource(value = {"0:0", "1:1", "9:9"}, delimiter = ':')
    void oneDigit(String input, int expected) {
        assertThat(Element.of(input).getElement()).isEqualTo(expected);
    }

    @DisplayName("빈 문자열 입력 시 0을 반환")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void emptyToZero(String input) {
        assertThat(Element.of(input).getElement()).isEqualTo(0);
    }

    @Test
    @DisplayName("null 입력 시 0을 반환")
    void nullToZero() {
        assertThat(Element.of(null).getElement()).isEqualTo(0);
    }

    @Test
    @DisplayName("음수 입력 시 IllegalArgumentException 발생")
    void negative() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Element.of("-1"));
    }

    @Test
    @DisplayName("숫자가 아닌 문자 입력 시 IllegalArgumentException 발생")
    void notNumber() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Element.of("a"));
    }
}