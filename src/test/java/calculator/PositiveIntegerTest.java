package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PositiveIntegerTest {
    @DisplayName("합을 계산한다.")
    @Test
    void sum() {
        String text = "1,2;3";

        PositiveInteger positiveInteger = new PositiveInteger(text, ",|;");

        assertThat(positiveInteger.sum()).isEqualTo(6);
    }
}
