package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PositiveIntegersTest {
    @DisplayName("합을 계산한다.")
    @Test
    void sum() {
        String text = "1,2;3";

        PositiveIntegers positiveIntegers = new PositiveIntegers(text, ",|;");

        assertThat(positiveIntegers.sum()).isEqualTo(6);
    }
}
