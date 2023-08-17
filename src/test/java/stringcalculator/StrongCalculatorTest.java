package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StrongCalculatorTest {

    @DisplayName("빈 문자열을 입력하면 결과는 0이다.")
    @Test
    void emptyString_thenZero() {
        StringCalculator stringCalculator = new StringCalculator();
        int result = stringCalculator.calculate("");
        assertThat(result).isZero();
    }
}

class StringCalculator {

    public int calculate(String str) {
        if ("".equals(str)) {
            return 0;
        }
        return 1;
    }
}
