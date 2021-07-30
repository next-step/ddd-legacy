package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class NumberTest {

    @DisplayName("숫자가 아닌 텍스트는 Number 로변환할 수 없다.")
    @Test
    void nan() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> Number.of("abc"));
    }

    @DisplayName("음수는 Number 로 변환할 수 없다.")
    @ParameterizedTest(name = "number: {0}")
    @ValueSource(strings = {"-1", "-2", "-3", "-4", "-5"})
    void negative(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> Number.of(text));
    }

    @DisplayName("Number 는 int 로 변환할 수 있다.")
    @ParameterizedTest(name = "number: {0}")
    @ValueSource(strings = {"1", "2", "3", "4", "5"})
    void toInteger(final String text) {
        assertThat(Number.of(text).toInteger())
                .isEqualTo(Integer.parseInt(text));
    }
}
