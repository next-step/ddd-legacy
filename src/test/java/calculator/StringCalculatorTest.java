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

    @Test
    @DisplayName("지정 구분자를 포함한 구분자 정규식을 찾는다")
    void getRegex() {
        assertThat(calculator.findDelimiter("//$\n1,2,3")).isEqualTo(",|:|$");
    }
}