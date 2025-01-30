package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class NumberTest {

    @DisplayName(value = "문자열 > 숫자로 변환")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void convertNumber(final String text) {
        assertThat(Number.convertNumber(text)).isEqualTo(1);
    }

    @DisplayName(value = "음수 입력 시, RuntimeException 예외 처리")
    @Test
    void negativeNumber() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> Number.convertNumber("-1"));
    }

    @DisplayName(value = "숫자 외 입력 시, RuntimeException 예외 처리")
    @ParameterizedTest
    @ValueSource(strings = {"ABC"})
    void invalidNumber(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> Number.convertNumber(text));
    }

}
