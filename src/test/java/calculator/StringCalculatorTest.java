package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringCalculatorTest {

    @DisplayName("빈 문자열 또는 null 전달시 0을 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void nullOrEmpty(String str) {
        StringCalculator calculator = new StringCalculator();

        int actual = calculator.run(str);

        assertThat(actual).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다")
    @Test
    void singleNumber() {
        StringCalculator calculator = new StringCalculator();

        int actual = calculator.run("123");

        assertThat(actual).isEqualTo(123);
    }

    @DisplayName("기본 구분자를 사용하여 계산 결과를 반환한다")
    @ParameterizedTest
    @CsvSource(
            value = {"1,2=3", "1,2,3=6", "1,2:3=6", "4:5:6=15"},
            delimiter = '='
    )
    void defaultDelimiter(String str, int expected) {
        StringCalculator calculator = new StringCalculator();

        int actual = calculator.run(str);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("음수 하나를 문자열로 입력할 경우 예외가 발생한다")
    @Test
    void singleNegativeNumber() {
        StringCalculator calculator = new StringCalculator();

        assertThatThrownBy(() -> calculator.run("-1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("음수가 포함된 경우 예외가 발생한다")
    @Test
    void negativeNumber() {
        StringCalculator calculator = new StringCalculator();

        assertThatThrownBy(() -> calculator.run("1,2,-3"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
