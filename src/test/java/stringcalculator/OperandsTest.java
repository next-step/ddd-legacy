package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OperandsTest {
    @DisplayName(value = "분리된 각 숫자의 합을 반환한다.")
    @Test
    void sum() {
        Operands operands = new Operands(new String[] {"1", "2", "3"});
        assertThat(operands.sum()).isEqualTo(6);
    }
}
