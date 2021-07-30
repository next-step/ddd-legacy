package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class OperandTest {

    @Test
    @DisplayName("integer 타입으로 피연산자를 생성할 수 있다.")
    void createByInt() {
        assertThat(new Operand(3)).isEqualTo(3);
        assertThat(new Operand(3)).isEqualTo(new Operand(3));
    }

    @Test
    @DisplayName("String 타입으로 피연산자를 생성할 수 있다.")
    void createByString() {
        assertThat(new Operand("3")).isEqualTo(new Operand("3"));
        assertThat(new Operand("3")).isEqualTo(new Operand(3));
    }

    @Test
    @DisplayName("숫자가 아닌 문자열로는 피연산자를 생성할 수 없다.")
    void validNumber() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Operand("aa"));
    }

    @Test
    @DisplayName("피연산자는 음수가 될 수 없다.")
    void validNegative() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Operand(-1));
    }

    @Test
    @DisplayName("피연산자간 연산을 할 수 있다.")
    void operate() {
        Operand operand1 = new Operand(3);
        Operand operand2 = new Operand(2);
        operand1.equals(3);
        assertThat(operand1.operate(operand2, (a,b) -> a+b)).isEqualTo(new Operand(5));
    }

}

