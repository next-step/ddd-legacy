package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;

public class CalculatorTest {

    public static final int DEFAULT_VALUE = 0;
    private static final String DEFAULT_NEGATIVE_VALUE = "-1";

    @ParameterizedTest
    @DisplayName(value = "빈문자열이거나 null이면 0 반환한다.")
    @NullAndEmptySource
    void nullAndEmptyValueTest(String operand) {
        assertThat(Calculator.calculate(operand)).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @DisplayName(value = "숫자 문자열 1개만 입력하면 해당 숫자 반환한다.")
    void singleValueTest() {
        assertThat(Calculator.calculate("1")).isEqualTo(1);
    }

    @Test
    @DisplayName(value = "음수 입력시 RuntimeException을 던진다.")
    void minusValueTest() {
        assertThatRuntimeException().isThrownBy(
                () -> Calculator.calculate(DEFAULT_NEGATIVE_VALUE)
        );
    }
}
