package calculator;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import exception.InvalidNumberFormatException;
import exception.NotPositiveNumberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositiveNumberTest {

    @DisplayName("음수 문자열을 전달하는 경우 NotPositiveNumberException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(NotPositiveNumberException.class)
            .isThrownBy(() -> new PositiveNumber("-1"));
    }

    @DisplayName("숫자가 아닌 문자열을 전달하는 경우 InvalidNumberFormatException 예외 처리를 한다.")
    @Test
    void notNumber() {
        assertThatExceptionOfType(InvalidNumberFormatException.class)
            .isThrownBy(() -> new PositiveNumber("notNumber"));
    }
}
