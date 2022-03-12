package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DelimiterTest {

    @DisplayName("value가 null이면 NullPointerException을 발생시킨다")
    @Test
    void nullValue() {
        // given

        // when & then
        assertThatThrownBy(() -> new Delimiter(null))
            .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("value의 사이즈가 1이 아니면 IllegalArgumentException을 발생시킨다")
    @ValueSource(strings = {"ab", "abc", "", "abcd"})
    @ParameterizedTest
    void wrongSize(final String value) {
        // given

        // when & then
        assertThatThrownBy(() -> new Delimiter(value))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("value가 숫자라면 IllegalArgumentException을 발생시킨다")
    @ValueSource(strings = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"})
    @ParameterizedTest
    void numberValue(final String value) {
        // given

        // when & then
        assertThatThrownBy(() -> new Delimiter(value))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("예약된 value라면 value 앞뒤에 \\Q와 \\E를 붙인다")
    @ValueSource(strings = {"<", "(", "[", "{", "\\", "^", "-", "=", "$", "!", "|", "]", "}", ")",
        "?", "+", ".", ">"})
    @ParameterizedTest
    void reservedWords(final String value) {
        // given

        // when
        final String result = new Delimiter(value)
            .getValue();

        // then
        assertThat(result).isEqualTo("\\Q" + value + "\\E");
    }

    @DisplayName("예약되지 않은 value라면 그것을 그대로 사용한다")
    @ValueSource(strings = {"a", "b", "c", "ㄱ"})
    @ParameterizedTest
    void noneReservedWords(final String value) {
        // given

        // when
        final String result = new Delimiter(value)
            .getValue();

        // then
        assertThat(result).isEqualTo(value);
    }
}
