package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

    @DisplayName("숫자 두개를 쉼표로 구분해서 입력할 경우 두 숫자의 합을 반환한다.")
    @CsvSource(value = {"1,2=3","2,3=5","3,7=10","5,8=13"}, delimiter = '=')
    @ParameterizedTest
    void two_string_sum(String input, Integer expected) {
        int result = stringCalculator.calculate(input);
        assertThat(result).isEqualTo(expected);
    }


}

class StringCalculator {

    public int calculate(String str) {
        if (StringUtils.isBlank(str)) {
            return 0;
        }

        String[] numbers = str.split(",");
        if (numbers.length == 1) {
            return convertNumber(numbers[0]);
        }

        return convertNumber(numbers[0]) + convertNumber(numbers[1]);
    }

    private int convertNumber(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            throw e;
        }
    }
}
