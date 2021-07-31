package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class OperandsTest {

    @Test
    @DisplayName("연산자를 담는 일급 컬렉션은 연산을 수행할 수 있다.")
    void calculate() {
        Operands operands = new Operands(Arrays.asList(new Operand(1), new Operand(2), new Operand(3)));
        assertThat(operands.calculate((a,b) -> a+b)).isSameAs(6);
    }

    @Test
    @DisplayName("문자열들로 만들어낸 연산자 컬렉션은 연산을 수행할 수 있다.")
    void calculateByStrings() {
        Operands operands = Operands.of(Arrays.asList("1", "2", "3"));
        assertThat(operands.calculate((a,b) -> a+b)).isSameAs(6);
    }

}
