package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OperandsTest {

    @DisplayName(value = "음수를 전달하는 경우 RuntimeException 예외를 throw 한다")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new Operands(new String[] {"1", "-11"}));
    }

    @DisplayName(value = "숫자가 아닌 문자를 전달하는 경우 RuntimeException 예외를 throw 한다")
    @Test
    void notNumber() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new Operands(new String[] {"1", "$"}));
    }

    @DisplayName(value = "Operands 의 합을 구한다.")
    @Test
    void sum() {
        assertThat(new Operands(new String[] {"1", "2", "7"}).sum()).isSameAs(10);
    }
}