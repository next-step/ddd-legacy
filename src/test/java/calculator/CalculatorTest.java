package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorTest {

    @Test
    @DisplayName("같은 값는 객체일 때")
    void validateBySameValueObject() {
        Calculator calculator = new Calculator("1");
        Calculator calculator2 = new Calculator("1");
        assertThat(calculator).isEqualTo(calculator2);
    }


}
