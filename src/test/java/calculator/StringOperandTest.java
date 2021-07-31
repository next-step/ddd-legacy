package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class StringOperandTest {
    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"1 1", "2 2", "3 3"}, delimiter = ' ')
    void parseInt(final String operand, final int expected) {
        assertThat(StringOperand.of(operand).parseInt()).isSameAs(expected);
    }

    @DisplayName(value = "음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void validateOperand() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> StringOperand.of("-1"));
    }
}
