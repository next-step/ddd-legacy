package calculator.number;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class PositiveNumberTest {

    @DisplayName(value = "음수일 경우, RuntimeException 발생한다")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new PositiveNumber("-1"));
    }

    @DisplayName(value = "숫자가 아닌 문자인 경우, RuntimeException 발생한다")
    @Test
    void text() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new PositiveNumber("a"));
    }
}
