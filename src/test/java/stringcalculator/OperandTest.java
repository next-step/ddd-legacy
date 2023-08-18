package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OperandTest {
    @DisplayName(value = "입력값으로 0 또는 양수가 주어지면 Operand 객체가 성공적으로 생성된다.")
    @ParameterizedTest
    @CsvSource(value = {"0,0", "3,3"})
    void positiveNumber(String strValue, int expected) {
        Operand operand = new Operand(strValue);
        assertThat(operand.getValue()).isEqualTo(expected);
    }

    @DisplayName(value = "입력값으로 음수가 주어지면 RuntimeException이 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-2"})
    void negativeNumber(String strValue) {
        assertThatThrownBy(() -> new Operand(strValue)).isInstanceOf(RuntimeException.class);
    }

    @DisplayName(value = "입력값으로 숫자가 아닌 값이 주어지면 RuntimeException이 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"a", " "})
    void character(String strValue) {
        assertThatThrownBy(() -> new Operand(strValue)).isInstanceOf(RuntimeException.class);
    }

}
