package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertThat(calculator.separateInputs("//$\n1,2$3")).containsExactly("1", "2", "3");
        assertThat(calculator.separateInputs("//--\n1,2--3")).containsExactly("1", "2", "3");
    }

    @Test
    @DisplayName("숫자 이외의 값 또는 음수는 RuntimeException을 발생한다.")
    void ExceptionWithNonNumeric() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.parseInt(Arrays.asList("-11", "2", "3")));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.parseInt(Arrays.asList("1", "2", "q")));
    }

    @Test
    @DisplayName("문자열 계산기의 결과값을 반환한다.")
    void calculateValues() {
        assertThat(calculator.calculate("//-\n1,2,3-4:5")).isEqualTo(15);
    }
}