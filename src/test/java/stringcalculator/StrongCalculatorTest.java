package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StrongCalculatorTest {

    @DisplayName("빈 문자열을 입력하면 결과는 0이다.")
    @Test
    void emptyString_thenZero() {
        StringCalculator stringCalculator = new StringCalculator();
        int result = stringCalculator.calculate("");
        assertThat(result).isZero();
    }

    @DisplayName("null 입력하면 결과는 0이다.")
    @Test
    void nullString_thenZero() {
        StringCalculator stringCalculator = new StringCalculator();
        int result = stringCalculator.calculate(null);
        assertThat(result).isZero();
    }

    @DisplayName("숫자 문자열 하나 입력시 입력한 숫자 문자열을 반환한다.")
    @Test
    void one_string_sum() {
        StringCalculator stringCalculator = new StringCalculator();
        int result = stringCalculator.calculate("1");
        assertThat(result).isEqualTo(1);
    }


}

class StringCalculator {

    public int calculate(String str) {
        if (StringUtils.isBlank(str)) {
            return 0;
        }
        return Integer.parseInt(str);
    }
}
