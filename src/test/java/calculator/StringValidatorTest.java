package calculator;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringValidatorTest {

    @DisplayName(value = "음수 문자열을 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> StringValidator.validateNumberAndPositive("-1"));
    }

    @DisplayName(value = "숫자가 아닌 문자열을 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void notNumber() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> StringValidator.validateNumberAndPositive("notNumber"));
    }

}
