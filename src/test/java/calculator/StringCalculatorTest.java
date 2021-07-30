package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StringCalculatorTest {
    @DisplayName("입력된 문자열이 null일 경우 0을 리턴한다.")
    @Test
    void inputNullTest() {
        StringCalculator calculator = new StringCalculator();
        int result = calculator.calculate(null);
        assertThat(result).isZero();
    }

    @DisplayName("입력된 문자열이 공백일 경우 0을 리턴한다.")
    @Test
    void inputEmptyTest() {
        StringCalculator calculator = new StringCalculator();
        int result = calculator.calculate("");
        assertThat(result).isZero();
    }

    @DisplayName("입력된 문자열이 숫자가 아니거나 음수인 경우 RuntimeException 발생")
    @Test
    void runtimeExceptionTest() {
        String text = "A,2,1";
        StringCalculator calculator = new StringCalculator();
        assertThatThrownBy(() -> calculator.calculate(text))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("쉼표(,)와 콜론(:)을 구분자로 가지는 문자열의 합을 구한다.")
    @Test
    void calculateTest() {
        String text = "1,2:3";
        StringCalculator calculator = new StringCalculator();
        int result = calculator.calculate(text);
        assertThat(result).isEqualTo(6);
    }

    @DisplayName("커스텀 구분자를 지정하여 문자열의 합을 구한다.")
    @Test
    void calculateWithCustomTest() {
        String text = "//;\n1;2;3";
        StringCalculator calculator = new StringCalculator();
        int result = calculator.calculate(text);
        assertThat(result).isEqualTo(6);
    }
}
