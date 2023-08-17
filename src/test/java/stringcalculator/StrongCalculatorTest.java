package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.platform.commons.util.StringUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StrongCalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp() {
        stringCalculator = new StringCalculator();
    }

    @DisplayName("빈 문자열 또는 null 을 입력하면 결과는 0이다.")
    @NullAndEmptySource
    @ParameterizedTest
    void empty_or_null_thenZero(String input) {
        int result = stringCalculator.calculate(input);
        assertThat(result).isZero();
    }

    @DisplayName("숫자 문자열 하나 입력시 입력한 숫자 문자열을 반환한다.")
    @Test
    void one_string_sum() {
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
