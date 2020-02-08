package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringCalculatorTest {

    @DisplayName("빈문자열이 들어오면 0을 리턴")
    @Test
    void addEmptyText() {
        assertThat(StringCalculator.add("")).isEqualTo(0);
    }

    @DisplayName("null 이 들어오면 0을 리턴")
    @Test
    void addNullText() {
        assertThat(StringCalculator.add(null)).isEqualTo(0);
    }

    @DisplayName("음수 혹은 숫자 이외의 값일 때 RunTimeException 을 던짐")
    @ParameterizedTest
    @ValueSource(strings = {"a", "-7"})
    void addNegative(String input) {
        assertThrows(RuntimeException.class, () -> StringCalculator.add(input));
    }

    @DisplayName("제대로 실행되는 케이스")
    @ParameterizedTest
    @ValueSource(strings = {"1:2:3", "1,2,3", "1,2:3", "//;\n1;2;3"})
    void add(String input) {
        assertThat(StringCalculator.add(input)).isEqualTo(6);
    }
}