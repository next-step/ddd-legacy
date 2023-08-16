package stringaddcalculator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OperandTest {

    @Test
    void Operand는_음수일_수_없다() {
        assertThatThrownBy(() -> new Operand(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void Operand는_더해진다() {
        assertThat(new Operand(1).plus(new Operand(1)))
                .isEqualTo(new Operand(2));
    }
}
