package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringCalculatorTest {
    private final StringCalculator calculator = new StringCalculator();

    @Test
    @DisplayName("구분자로 숫자를 구분한다.")
    void findDefaultSeparator() {
        assertThat(calculator.separateInputs("1,2,3")).containsExactly("1", "2", "3");
    }
}