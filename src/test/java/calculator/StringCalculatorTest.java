package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class StringCalculatorTest {

    @DisplayName("빈 문자열 또는 null 전달시 0을 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void nullOrEmpty(String str) {
        StringCalculator calculator = new StringCalculator();
        assertThat(calculator.run(str)).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다")
    @Test
    void singleNumber() {
        StringCalculator calculator = new StringCalculator();
        assertThat(calculator.run("123")).isEqualTo(123);
    }
}
