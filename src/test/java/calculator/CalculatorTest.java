package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @DisplayName("문자열이 숫자하나만 들어왔을 경우 숫자를 반환한다.")
    @ValueSource(strings = {"1", "22", "333"})
    @ParameterizedTest
    void onlyNumberCheck(String input) {
        assertThat(calculator.calculate(input)).isEqualTo(Integer.parseInt(input));
    }

    @DisplayName("문자열이 하나만 들어왔을 경우 숫자가 아니라면 예외를 발생시킨다.")
    @ValueSource(strings = {"a", "aa", "a1a"})
    @ParameterizedTest
    void onlyNumberExceptionCheck(String input) {
        assertThatThrownBy(() -> calculator.calculate(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("숫자의 형태가 아닙니다.");
    }

    @DisplayName("음수를 전달할 경우 IllegalArgumentException 예외가 발생해야 한다.")
    @ValueSource(strings = {"-1,,1", "1,-2:3", "//;\\n1;;2;;-3"})
    @ParameterizedTest
    void minusNumberCheck(String input) {
        assertThatThrownBy(() -> calculator.calculate(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음수는 들어올 수 없습니다.");
    }

}
