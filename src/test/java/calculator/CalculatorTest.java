package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorTest {

    @Test
    @DisplayName("같은 값는 객체일 때")
    void validateBySameValueObject() {
        Calculator calculator = new Calculator("1");
        Calculator calculator2 = new Calculator("1");
        assertThat(calculator).isEqualTo(calculator2);
    }

    @Test
    @DisplayName("빈 문자열 또는 null 경우 0 반환")
    void getZeroByNullOrEmpty() {
        Calculator calculator = new Calculator("");
        Calculator calculator1 = new Calculator(null);

        assertThat(calculator.operate()).isEqualTo(0);
        assertThat(calculator1.operate()).isEqualTo(0);
    }

    @Test
    @DisplayName("숫자 하나만 넣었을 경우")
    void checkInputNumberBySizeOne() {
        Calculator calculator = new Calculator("9");

        assertThat(calculator.operate()).isEqualTo(9);
    }
}
