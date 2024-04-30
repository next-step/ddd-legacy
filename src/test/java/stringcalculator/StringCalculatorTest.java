package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringCalculatorTest {
    @Test
    @DisplayName("빈 스트링을 넣는 경우 0을 반환한다.")
    void zero() {
        String input = "";
        assertThat(StringCalculator.getSum(input)).isEqualTo(0);
    }

    @Test
    @DisplayName("쉼표가 포함된 문자열인 경우 분리한 숫자 합을 반환한다.")
    void commaSum() {
        String input = "1,2,3";
        assertThat(StringCalculator.getSum(input)).isEqualTo(6);
    }

    @Test
    @DisplayName("콜론이 포함된 문자열인 경우 분리한 숫자 합을 반환한다.")
    void colonSum() {
        String input = "1:2:3";
        assertThat(StringCalculator.getSum(input)).isEqualTo(6);
    }

    @Test
    @DisplayName("쉼표, 콜론이 같이 있는 경우 숫자 합을 반환한다.")
    void mixSum() {
        String input = "1:2,3";
        assertThat(StringCalculator.getSum(input)).isEqualTo(6);
    }
}
