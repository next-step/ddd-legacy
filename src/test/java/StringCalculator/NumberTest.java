package StringCalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class NumberTest {

    @DisplayName(value = "문자열을 숫자로 변환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void create(final String text) {
        Number number = new Number(text);
        assertThat(number.getValue()).isEqualTo(Integer.parseInt(text));
    }

    @DisplayName(value = "음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new Number("-1"));
    }
} 