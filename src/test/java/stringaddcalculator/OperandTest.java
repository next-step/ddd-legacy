package stringaddcalculator;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class OperandTest {

    @ValueSource(ints = {0, 1})
    @ParameterizedTest
    void Operand는_0_또는_양수이다(int number) {
        assertDoesNotThrow(() -> new Operand(number));
    }

    @Test
    void Operand는_음수일_수_없다() {
        assertThatThrownBy(() -> new Operand(-1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void Operand는_더해진다() {
        assertThat(new Operand(1).plus(new Operand(1)))
                .isEqualTo(new Operand(2));
    }
}
